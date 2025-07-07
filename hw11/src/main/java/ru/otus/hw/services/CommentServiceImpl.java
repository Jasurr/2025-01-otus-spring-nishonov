package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

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
    public Mono<CommentDto> findById(String id) {
        return Mono.defer(() -> {
            if (id == null || id.trim().isEmpty()) {
                return Mono.error(new IllegalArgumentException(ERROR_INVALID_ID));
            }
            return commentRepository.findById(id)
                    .switchIfEmpty(Mono.error(new EntityNotFoundException(ERROR_COMMENT_NOT_FOUND.formatted(id))))
                    .map(commentMapper::toDto);
        });
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<CommentDto> findByBookId(String bookId) {
        return commentRepository.findByBookId(bookId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(ERROR_COMMENT_NOT_FOUND.formatted(bookId))))
                .map(commentMapper::toDto);
    }

    @Override
    public Mono<CommentDto> insert(String message, String bookId) {
        if (message == null || message.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(ERROR_INVALID_MESSAGE));
        }
        if (bookId == null || bookId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(ERROR_INVALID_ID));
        }
        return findBook(bookId)
                .flatMap(book -> {
                    var comment = new Comment();
                    comment.setMessage(message);
                    comment.setBook(book);
                    return commentRepository.save(comment);
                })
                .map(commentMapper::toDto);
    }

    @Transactional
    @Override
    public Mono<CommentDto> update(String id, String message) {
        if (message == null || message.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(ERROR_INVALID_MESSAGE));
        }
        if (id == null || id.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(ERROR_INVALID_ID));
        }
        return commentRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(ERROR_COMMENT_NOT_FOUND.formatted(id))))
                .flatMap(comment -> {
                    comment.setMessage(message);
                    return commentRepository.save(comment);
                })
                .map(commentMapper::toDto);
    }

    @Transactional
    @Override
    public Mono<Void> deleteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(ERROR_INVALID_ID));
        }
        return commentRepository.deleteById(id);
    }

    private Mono<Book> findBook(String bookId) {
        return bookRepository.findById(bookId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(ERROR_BOOK_NOT_FOUND.formatted(bookId))));
    }
}