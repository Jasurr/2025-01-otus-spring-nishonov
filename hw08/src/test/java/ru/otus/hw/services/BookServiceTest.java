package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DisplayName("Book Service Tests")
public class BookServiceTest {

    private static final String FIRST_AUTHOR_ID = "1";
    private static final String FIRST_GENRE_ID = "1";
    private static final String TEST_BOOK_TITLE = "Test Book";
    private static final String UPDATED_BOOK_TITLE = "Updated Book";

    @Autowired
    private BookService bookService;
    @Autowired
    private MongoTemplate mongoTemplate;

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
    protected Author getAuthorById(String authorId) {
        var author = mongoTemplate.findById(authorId, Author.class);
        assertNotNull(author, "Author with ID " + authorId + " not found");
        return author;
    }

    @Transactional(readOnly = true)
    protected Genre getGenreById(String genreId) {
        var genre = mongoTemplate.findById(genreId, Genre.class);
        assertNotNull(genre, "Genre with ID " + genreId + " not found");
        return genre;
    }
}
