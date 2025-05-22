package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentDto> findById(long id) {
        return commentRepository.findById(id)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> findByBookId(long bookId) {
        return commentRepository.findByBookId(bookId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    @Override
    public CommentDto insert(String message, long bookId) {
        var book = bookRepository.findById(bookId);
        var comment = new Comment();
        comment.setMessage(message);
        if (book.isPresent()) {
            comment.setBook(book.get());
        } else {
            throw new EntityNotFoundException("Book not found");
        }
        return toDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentDto update(long id, String message) {
        var commentOptional = commentRepository.findById(id);
        if (commentOptional.isPresent()) {
            var comment = commentOptional.get();
            comment.setMessage(message);
            return toDto(commentRepository.save(comment));
        } else {
            throw new EntityNotFoundException("Comment not found");
        }
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }

    private CommentDto toDto(Comment comment) {
        var dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setMessage(comment.getMessage());
        dto.setBook(comment.getBook());
        return dto;
    }
}
