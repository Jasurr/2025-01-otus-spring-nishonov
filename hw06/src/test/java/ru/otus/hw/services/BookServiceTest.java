package ru.otus.hw.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.JpaAuthorRepository;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaGenreRepository;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Import({BookServiceImpl.class, JpaBookRepository.class, JpaAuthorRepository.class, JpaGenreRepository.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DisplayName("BookService Tests")
class BookServiceTest {
    private static final long FIRST_AUTHOR_ID = 1L;
    private static final long FIRST_GENRE_ID = 1L;
    private static final String BOOK_TITLE = "Test Book";
    private static final String UPDATED_BOOK_TITLE = "Updated Book";

    @Autowired
    private BookService bookService;

    @PersistenceContext
    private EntityManager em;

    private Author author;
    private Genre genre;

    @BeforeEach
    @Transactional(readOnly = true)
    @DisplayName("Set up common test data")
    void setUp() {
        author = getAuthorById(FIRST_AUTHOR_ID);
        genre = getGenreById(FIRST_GENRE_ID);
    }

    @Test
    @DisplayName("Should find book by ID without LazyInitializationException")
    void shouldFindBookById() {
        var savedBook = bookService.insert(BOOK_TITLE, author.getId(), Set.of(genre.getId()));
        var foundBook = bookService.findById(savedBook.getId());

        assertThat(foundBook)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("title", BOOK_TITLE)
                .hasFieldOrPropertyWithValue("author.id", author.getId())
                .satisfies(book -> assertThat(book.getGenres()).anyMatch(g -> g.getId() == genre.getId()));
    }

    @Test
    @DisplayName("Should find all books without LazyInitializationException")
    void shouldFindAllBooks() {
        var books = bookService.findAll();
        assertThat(books)
                .isNotEmpty()
                .allMatch(book -> book.getAuthor() != null && book.getGenres() != null);
    }

    @Test
    @DisplayName("Should insert book correctly")
    void shouldInsertBook() {
        var insertedBook = bookService.insert(BOOK_TITLE, author.getId(), Set.of(genre.getId()));

        assertThat(insertedBook)
                .isNotNull()
                .hasFieldOrPropertyWithValue("title", BOOK_TITLE)
                .hasFieldOrPropertyWithValue("author.id", author.getId())
                .satisfies(book -> assertThat(book.getGenres()).anyMatch(g -> g.getId() == genre.getId()));
    }

    @Test
    @DisplayName("Should update book title")
    void shouldUpdateBook() {
        var insertedBook = bookService.insert(BOOK_TITLE, author.getId(), Set.of(genre.getId()));
        var updatedBook = bookService.update(insertedBook.getId(), UPDATED_BOOK_TITLE, author.getId(), Set.of(genre.getId()));

        assertThat(updatedBook)
                .isNotNull()
                .hasFieldOrPropertyWithValue("title", UPDATED_BOOK_TITLE)
                .hasFieldOrPropertyWithValue("author.id", author.getId())
                .satisfies(book -> assertThat(book.getGenres()).anyMatch(g -> g.getId() == genre.getId()));
    }

    @Test
    @DisplayName("Should delete book from database")
    void shouldDeleteBook() {
        var insertedBook = bookService.insert(BOOK_TITLE, author.getId(), Set.of(genre.getId()));
        bookService.deleteById(insertedBook.getId());

        var deletedBook = bookService.findById(insertedBook.getId());
        assertThat(deletedBook).isEmpty();
    }

    @Transactional(readOnly = true)
    protected Author getAuthorById(long authorId) {
        var author = em.find(Author.class, authorId);
        assertNotNull(author, "Author with ID " + authorId + " not found");
        return author;
    }

    @Transactional(readOnly = true)
    protected Genre getGenreById(long genreId) {
        var genre = em.find(Genre.class, genreId);
        assertNotNull(genre, "Genre with ID " + genreId + " not found");
        return genre;
    }
}