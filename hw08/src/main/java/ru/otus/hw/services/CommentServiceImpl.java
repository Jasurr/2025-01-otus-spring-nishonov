package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public Optional<Comment> findById(String id) {
        return commentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Comment> findByBookId(String bookId) {
        return commentRepository.findByBookId(bookId);
    }

    @Transactional
    @Override
    public Comment insert(String message, String bookId) {
        var book = bookRepository.findById(bookId);
        var comment = new Comment();
        comment.setMessage(message);
        if (book.isPresent()) {
            comment.setBook(book.get());
        } else {
            throw new EntityNotFoundException("Book not found");
        }
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment update(String id, String message, String bookId) {
        var comments = commentRepository.findById(id);
        if (comments.isPresent()) {
            var comment = comments.get();
            comment.setMessage(message);
            var book = bookRepository.findById(bookId);
            if (book.isPresent()) {
                comment.setBook(book.get());
            } else {
                throw new EntityNotFoundException("Book not found");
            }
            return commentRepository.save(comment);
        } else {
            throw new EntityNotFoundException("Comment not found");
        }
    }

    @Transactional
    @Override
    public void deleteById(String id) {
        commentRepository.deleteById(id);
    }
}
