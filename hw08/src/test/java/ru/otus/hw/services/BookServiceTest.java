package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw.config.TestMongockConfig;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import({TestMongockConfig.class, BookServiceImpl.class, BookMapper.class})
@DisplayName("Book Service Tests for MongoDB")
class BookServiceTest {

    private static final String TEST_BOOK_TITLE = "Test Book";

    private static final String UPDATED_BOOK_TITLE = "Updated Book";

    private static final String TEST_AUTHOR_NAME = "Author 1";

    private static final String TEST_GENRE_NAME = "Genre 1";

    @Autowired
    private BookService bookService;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Author author;
    private Genre genre;

    @BeforeEach
    @DisplayName("Setup common test data")
    void setUp() {
        // Fetch Author and Genre from migrated data
        author = mongoTemplate.findOne(
                Query.query(Criteria.where("fullName").is(TEST_AUTHOR_NAME)), Author.class);
        assertThat(author)
                .as("Author with name " + TEST_AUTHOR_NAME + " not found")
                .isNotNull();

        genre = mongoTemplate.findOne(
                Query.query(Criteria.where("name").is(TEST_GENRE_NAME)), Genre.class);
        assertThat(genre)
                .as("Genre with name " + TEST_GENRE_NAME + " not found")
                .isNotNull();
    }

    @Test
    @DisplayName("Find book by ID should return correct book")
    void shouldFindBookById() {
        var savedBook = bookService.insert(TEST_BOOK_TITLE, author.getId(), Set.of(genre.getId()));
        var foundBook = bookService.findById(savedBook.id());

        assertThat(foundBook)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("title", TEST_BOOK_TITLE)
                .hasFieldOrPropertyWithValue("author.id", author.getId())
                .satisfies(book -> assertThat(book.genres()).anyMatch(g -> g.id().equals(genre.getId())));
    }

    @Test
    @DisplayName("Find all books should return non-empty list")
    void shouldFindAllBooks() {
        var books = bookService.findAll();
        assertThat(books)
                .isNotEmpty()
                .allMatch(book -> book.author() != null && book.genres() != null);
    }

    @Test
    @DisplayName("Insert book should create a new book")
    void shouldInsertBook() {
        var insertedBook = bookService.insert(TEST_BOOK_TITLE, author.getId(), Set.of(genre.getId()));

        assertThat(insertedBook)
                .isNotNull()
                .hasFieldOrPropertyWithValue("title", TEST_BOOK_TITLE)
                .hasFieldOrPropertyWithValue("author.id", author.getId())
                .satisfies(book -> assertThat(book.genres()).anyMatch(g -> g.id().equals(genre.getId())));
    }

    @Test
    @DisplayName("Update book should modify book title")
    void shouldUpdateBook() {
        var insertedBook = bookService.insert(TEST_BOOK_TITLE, author.getId(), Set.of(genre.getId()));
        var updatedBook = bookService.update(insertedBook.id(), UPDATED_BOOK_TITLE, author.getId(), Set.of(genre.getId()));

        assertThat(updatedBook)
                .isNotNull()
                .hasFieldOrPropertyWithValue("title", UPDATED_BOOK_TITLE)
                .hasFieldOrPropertyWithValue("author.id", author.getId())
                .satisfies(book -> assertThat(book.genres()).anyMatch(g -> g.id().equals(genre.getId())));
    }

    @Test
    @DisplayName("Delete book should remove book by ID")
    void shouldDeleteBook() {
        var insertedBook = bookService.insert(TEST_BOOK_TITLE, author.getId(), Set.of(genre.getId()));
        bookService.deleteById(insertedBook.id());

        var deletedBook = bookService.findById(insertedBook.id());
        assertThat(deletedBook).isEmpty();
    }
}