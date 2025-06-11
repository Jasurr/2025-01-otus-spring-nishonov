package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    public Optional<BookDto> findById(String id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public BookDto insert(String title, String authorId, Set<String> genresIds) {
        return save(null, title, authorId, Set.copyOf(genresIds));
    }

    @Transactional
    @Override
    public BookDto update(String id, String title, String authorId, Set<String> genresIds) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_INVALID_ID);
        }
        var updatedBookDto = save(id, title, authorId, Set.copyOf(genresIds));

        updateCommentsWithBook(id, updatedBookDto);

        return updatedBookDto;
    }

    @Transactional
    @Override
    public void deleteById(String id) {
        commentRepository.deleteByBookId(id);

        bookRepository.deleteById(id);
    }

    private BookDto save(String id, String title, String authorId, Set<String> genresIds) {
        validateInput(title, authorId, genresIds);

        Author author = findAuthor(authorId);
        List<Genre> genres = findGenres(genresIds);
        Book book = id == null ? new Book() : bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ERROR_BOOK_NOT_FOUND.formatted(id)));

        book.setTitle(title);
        book.setAuthor(author);
        book.setGenres(genres);

        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }

    private Author findAuthor(String authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException(ERROR_AUTHOR_NOT_FOUND.formatted(authorId)));
    }

    private List<Genre> findGenres(Set<String> genresIds) {
        List<Genre> genres = genreRepository.findAllByIdIn(genresIds);
        if (genres.size() != genresIds.size()) {
            throw new EntityNotFoundException(ERROR_GENRES_NOT_FOUND.formatted(genresIds));
        }
        return genres;
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

    private void updateCommentsWithBook(String bookId, BookDto updatedBookDto) {
        Book updatedBook = bookMapper.toEntity(updatedBookDto);
        commentRepository.updateCommentsByBookId(bookId, updatedBook);
    }
}