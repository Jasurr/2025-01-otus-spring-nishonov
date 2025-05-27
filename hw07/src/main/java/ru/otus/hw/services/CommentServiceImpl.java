package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.SimpleCommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.CommentMapper;
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

    private final CommentMapper commentMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<SimpleCommentDto> findById(long id) {
        return commentRepository.findById(id)
                .map(comment -> new SimpleCommentDto(comment.getId(), comment.getMessage()));
    }

    @Transactional(readOnly = true)
    @Override
    public List<SimpleCommentDto> findByBookId(long bookId) {
        return commentRepository.findByBookId(bookId)
                .stream()
                .map(comment -> new SimpleCommentDto(comment.getId(), comment.getMessage()))
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
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentDto update(long id, String message) {
        return commentRepository.findById(id)
                .map(comment -> {
                    comment.setMessage(message);
                    return commentMapper.toDto(commentRepository.save(comment));
                })
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }
}
