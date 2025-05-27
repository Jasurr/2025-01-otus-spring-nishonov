package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private static final String ERROR_BOOK_NOT_FOUND = "Book with id %s not found";

    private static final String ERROR_COMMENT_NOT_FOUND = "Comment with id %s not found";

    private static final String ERROR_INVALID_ID = "ID must not be null or empty";

    private static final String ERROR_INVALID_MESSAGE = "Message must not be null or empty";

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    @Override
    public Optional<CommentDto> findById(String id) {
        validateId(id);
        return commentRepository.findById(id).map(commentMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> findByBookId(String bookId) {
        validateId(bookId);
        return commentRepository.findByBookId(bookId)
                .stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public CommentDto insert(String message, String bookId) {
        validateInput(message, bookId);
        Book book = findBook(bookId);
        Comment comment = new Comment();
        comment.setMessage(message);
        comment.setBook(book);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public CommentDto update(String id, String message) {
        validateId(id);
        validateMessage(message);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ERROR_COMMENT_NOT_FOUND.formatted(id)));
        comment.setMessage(message);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public void deleteById(String id) {
        validateId(id);
        commentRepository.deleteById(id);
    }

    private Book findBook(String bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(ERROR_BOOK_NOT_FOUND.formatted(bookId)));
    }

    private void validateInput(String message, String bookId) {
        validateMessage(message);
        validateId(bookId);
    }

    private void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_INVALID_ID);
        }
    }

    private void validateMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_INVALID_MESSAGE);
        }
    }
}