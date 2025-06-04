package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private static final String ERROR_BOOK_NOT_FOUND = "Book with id %s not found";

    private static final String ERROR_AUTHOR_NOT_FOUND = "Author with id %s not found";

    private static final String ERROR_GENRES_NOT_FOUND = "One or more genres with ids %s not found";

    private static final String ERROR_INVALID_TITLE = "Title must not be null or empty";

    private static final String ERROR_INVALID_AUTHOR_ID = "Author ID must not be null";

    private static final String ERROR_INVALID_GENRES = "Genres IDs must not be null or empty";

    private static final String ERROR_INVALID_ID = "ID must not be null or empty for update";

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    private final BookMapper bookMapper;

    @Override
    public Mono<BookDto> findById(String id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<BookDto> findAll() {
        return bookRepository.findAll()
                .map(bookMapper::toDto);
    }

    @Transactional
    @Override
    public Mono<BookDto> insert(String title, String authorId, Set<String> genresIds) {
        return save(null, title, authorId, Set.copyOf(genresIds));
    }

    @Transactional
    @Override
    public Mono<BookDto> update(String id, String title, String authorId, Set<String> genresIds) {
        if (id == null || id.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(ERROR_INVALID_ID));
        }

        return save(id, title, authorId, Set.copyOf(genresIds))
                .flatMap(updatedBookDto ->
                        updateCommentsWithBook(id, updatedBookDto)
                                .thenReturn(updatedBookDto)
                );
    }


    @Transactional
    @Override
    public Mono<Void> deleteById(String id) {
        return commentRepository.deleteByBookId(id)
                .then(bookRepository.deleteById(id));
    }

    private Mono<BookDto> save(String id, String title, String authorId, Set<String> genresIds) {
        validateInput(title, authorId, genresIds);

        Mono<Author> authorMono = findAuthor(authorId);
        Mono<List<Genre>> genresListMono = findGenres(genresIds).collectList()
                .flatMap(genres -> {
                    if (genres.size() != genresIds.size()) {
                        return Mono.error(new EntityNotFoundException(ERROR_GENRES_NOT_FOUND.formatted(genresIds)));
                    }
                    return Mono.just(genres);
                });

        Mono<Book> bookMono = (id == null)
                ? Mono.just(new Book())
                : bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(ERROR_BOOK_NOT_FOUND.formatted(id))));

        return Mono.zip(bookMono, authorMono, genresListMono)
                .flatMap(tuple -> {
                    Book book = tuple.getT1();
                    Author author = tuple.getT2();
                    List<Genre> genres = tuple.getT3();

                    book.setTitle(title);
                    book.setAuthor(author);
                    book.setGenres(genres);

                    return bookRepository.save(book);
                })
                .map(bookMapper::toDto); // if toDto returns Mono<BookDto>
    }


    private Mono<Author> findAuthor(String authorId) {
        return authorRepository.findById(authorId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(ERROR_AUTHOR_NOT_FOUND.formatted(authorId))));
    }

    private Flux<Genre> findGenres(Set<String> genresIds) {
        return genreRepository.findAllByIdIn(genresIds)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(ERROR_GENRES_NOT_FOUND.formatted(genresIds))));
    }

    private void validateInput(String title, String authorId, Set<String> genresIds) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_INVALID_TITLE);
        }
        if (authorId == null || authorId.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_INVALID_AUTHOR_ID);
        }
        if (genresIds == null || genresIds.isEmpty()) {
            throw new IllegalArgumentException(ERROR_INVALID_GENRES);
        }
    }

    private Mono<Void> updateCommentsWithBook(String bookId, BookDto updatedBookDto) {
        Book updatedBook = bookMapper.toEntity(updatedBookDto);

        return commentRepository.findByBookId(bookId) // Flux<Comment>
                .flatMap(comment -> {
                    comment.setBook(updatedBook);
                    return commentRepository.save(comment); // Mono<Comment>
                })
                .then();
    }

}