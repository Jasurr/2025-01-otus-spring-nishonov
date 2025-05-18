package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDTO;
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
    public Optional<CommentDTO> findById(long id) {
        return commentRepository.findById(id)
                .map(comment -> {
                    var commentDTO = new CommentDTO();
                    commentDTO.setId(comment.getId());
                    commentDTO.setMessage(comment.getMessage());
                    commentDTO.setBookId(comment.getBook().getId());
                    return commentDTO;
                });
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDTO> findByBookId(long bookId) {
        return commentRepository.findByBookId(bookId)
                .stream()
                .map(comment -> {
                    var commentDTO = new CommentDTO();
                    commentDTO.setId(comment.getId());
                    commentDTO.setMessage(comment.getMessage());
                    commentDTO.setBookId(comment.getBook().getId());
                    return commentDTO;
                })
                .toList();
    }

    @Transactional
    @Override
    public CommentDTO insert(String message, long bookId) {
        var book = bookRepository.findById(bookId);
        var comment = new Comment();
        comment.setMessage(message);
        if (book.isPresent()) {
            comment.setBook(book.get());
        } else {
            throw new EntityNotFoundException("Book not found");
        }

        commentRepository.save(comment);
        var commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setMessage(comment.getMessage());
        commentDTO.setBookId(comment.getBook().getId());
        return commentDTO;
    }

    @Override
    @Transactional
    public CommentDTO update(long id, String message, long bookId) {
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
            commentRepository.save(comment);
            var commentDTO = new CommentDTO();
            commentDTO.setId(comment.getId());
            commentDTO.setMessage(comment.getMessage());
            commentDTO.setBookId(comment.getBook().getId());
            return commentDTO;
        } else {
            throw new EntityNotFoundException("Comment not found");
        }
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }
}
