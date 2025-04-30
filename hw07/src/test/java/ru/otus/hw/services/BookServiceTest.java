package ru.otus.hw.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(BookServiceImpl.class)
@DisplayName("Book Service Tests")
class BookServiceTest {
    private static final long FIRST_AUTHOR_ID = 1L;
    private static final long FIRST_GENRE_ID = 1L;
    private static final String TEST_BOOK_TITLE = "Test Book";
    private static final String UPDATED_BOOK_TITLE = "Updated Book";

    @Autowired
    private BookService bookService;
    @PersistenceContext
    private EntityManager em;

    private Author author;
    private Genre genre;

    @BeforeEach
    @DisplayName("Setup common test data")
    void setUp() {
        // Preload common test data
        author = getAuthorById(FIRST_AUTHOR_ID);
        genre = getGenreById(FIRST_GENRE_ID);
    }

    @Test
    @DisplayName("Find book by ID should return correct book")
    void shouldFindBookById() {
        var savedBook = bookService.insert(TEST_BOOK_TITLE, author.getId(), Set.of(genre.getId()));
        var foundBook = bookService.findById(savedBook.getId());

        assertThat(foundBook)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("title", TEST_BOOK_TITLE)
                .hasFieldOrPropertyWithValue("author.id", author.getId())
                .satisfies(book -> assertThat(book.getGenres()).anyMatch(g -> g.getId() == genre.getId()));
    }

    @Test
    @DisplayName("Find all books should return non-empty list")
    void shouldFindAllBooks() {
        var books = bookService.findAll();
        assertThat(books)
                .isNotEmpty()
                .allMatch(book -> book.getAuthor() != null && book.getGenres() != null);
    }

    @Test
    @DisplayName("Insert book should create a new book")
    void shouldInsertBook() {
        var insertedBook = bookService.insert(TEST_BOOK_TITLE, author.getId(), Set.of(genre.getId()));

        assertThat(insertedBook)
                .isNotNull()
                .hasFieldOrPropertyWithValue("title", TEST_BOOK_TITLE)
                .hasFieldOrPropertyWithValue("author.id", author.getId())
                .satisfies(book -> assertThat(book.getGenres()).anyMatch(g -> g.getId() == genre.getId()));
    }

    @Test
    @DisplayName("Update book should modify book title")
    void shouldUpdateBook() {
        var insertedBook = bookService.insert(TEST_BOOK_TITLE, author.getId(), Set.of(genre.getId()));
        var updatedBook = bookService.update(insertedBook.getId(), UPDATED_BOOK_TITLE, author.getId(), Set.of(genre.getId()));

        assertThat(updatedBook)
                .isNotNull()
                .hasFieldOrPropertyWithValue("title", UPDATED_BOOK_TITLE)
                .hasFieldOrPropertyWithValue("author.id", author.getId())
                .satisfies(book -> assertThat(book.getGenres()).anyMatch(g -> g.getId() == genre.getId()));
    }

    @Test
    @DisplayName("Delete book should remove book by ID")
    void shouldDeleteBook() {
        var insertedBook = bookService.insert(TEST_BOOK_TITLE, author.getId(), Set.of(genre.getId()));
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