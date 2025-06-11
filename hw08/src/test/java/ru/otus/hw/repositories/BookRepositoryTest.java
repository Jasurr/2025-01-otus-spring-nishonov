package ru.otus.hw.repositories;

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
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import(TestMongockConfig.class)
@DisplayName("Book Repository Tests for MongoDB")
class BookRepositoryTest {

    private static final String TEST_BOOK_TITLE = "Sample Book";
    private static final String TEST_BOOK_TITLE_2 = "Sample Book 2";
    private static final String TEST_AUTHOR_NAME = "Author 1";
    private static final String TEST_GENRE_NAME = "Genre 1";

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Author testAuthor;
    private Genre testGenre;

    @BeforeEach
    void setUp() {
        // Clean up any existing test data
        mongoTemplate.remove(Query.query(Criteria.where("title").regex("^" + TEST_BOOK_TITLE)), Book.class);

        // Fetch reference data once
        testAuthor = findAuthorByName();
        testGenre = findGenreByName();
    }

    @Test
    @DisplayName("Should save a book successfully")
    void shouldSaveBook() {
        // Given
        Book book = createTestBook(TEST_BOOK_TITLE);

        // When
        Book savedBook = bookRepository.save(book);

        // Then
        assertBookIsSavedCorrectly(savedBook);
        verifyBookExistsInDatabase(savedBook);
    }

    @Test
    @DisplayName("Should find a book by ID")
    void shouldFindById() {
        // Given - use MongoTemplate to set up test data
        Book testBook = createTestBook(TEST_BOOK_TITLE);
        Book savedBook = mongoTemplate.save(testBook);

        // When - test the repository method
        Optional<Book> foundBook = bookRepository.findById(savedBook.getId());

        // Then - verify using assertions
        assertThat(foundBook)
                .isPresent()
                .get()
                .satisfies(book -> assertBooksAreEqual(book, savedBook));
    }

    @Test
    @DisplayName("Should find all books with complete data")
    void shouldFindAll() {
        // Given - use MongoTemplate to set up test data
        mongoTemplate.save(createTestBook(TEST_BOOK_TITLE));
        mongoTemplate.save(createTestBook(TEST_BOOK_TITLE_2));

        // When - test the repository method
        List<Book> allBooks = bookRepository.findAll();

        // Then
        assertThat(allBooks)
                .hasSizeGreaterThanOrEqualTo(2)
                .allSatisfy(this::assertBookHasCompleteData)
                .extracting(Book::getTitle)
                .contains(TEST_BOOK_TITLE, TEST_BOOK_TITLE_2);
    }

    @Test
    @DisplayName("Should delete a book by ID")
    void shouldDeleteById() {
        // Given - use MongoTemplate to set up test data
        Book testBook = createTestBook(TEST_BOOK_TITLE);
        Book savedBook = mongoTemplate.save(testBook);
        String bookId = savedBook.getId();

        // When - test the repository method
        bookRepository.deleteById(bookId);

        // Then - verify using MongoTemplate
        Book deletedBook = mongoTemplate.findById(bookId, Book.class);
        assertThat(deletedBook).isNull();
    }

    @Test
    @DisplayName("Should handle non-existent book ID gracefully")
    void shouldHandleNonExistentId() {
        // Given
        String nonExistentId = "507f1f77bcf86cd799439011";

        // When & Then - test repository method with non-existent ID
        assertThat(bookRepository.findById(nonExistentId)).isEmpty();

        // Verify using MongoTemplate as well
        Book foundBook = mongoTemplate.findById(nonExistentId, Book.class);
        assertThat(foundBook).isNull();
    }

    // Helper methods
    private Author findAuthorByName() {
        Author author = mongoTemplate.findOne(
                Query.query(Criteria.where("fullName").is(TEST_AUTHOR_NAME)), Author.class);
        assertThat(author)
                .as("Author with name '%s' should exist in test data", TEST_AUTHOR_NAME)
                .isNotNull();
        return author;
    }

    private Genre findGenreByName() {
        Genre genre = mongoTemplate.findOne(
                Query.query(Criteria.where("name").is(TEST_GENRE_NAME)), Genre.class);
        assertThat(genre)
                .as("Genre with name '%s' should exist in test data", TEST_GENRE_NAME)
                .isNotNull();
        return genre;
    }

    private Book createTestBook(String title) {
        return new Book(null, title, testAuthor, List.of(testGenre));
    }

    private void assertBookIsSavedCorrectly(Book savedBook) {
        assertThat(savedBook.getId())
                .as("Saved book should have a generated ID")
                .isNotNull()
                .isNotBlank();

        assertThat(savedBook.getTitle())
                .as("Saved book should have correct title")
                .isEqualTo(TEST_BOOK_TITLE);

        assertThat(savedBook.getAuthor())
                .as("Saved book should have correct author")
                .isEqualTo(testAuthor);

        assertThat(savedBook.getGenres())
                .as("Saved book should have correct genres")
                .containsExactly(testGenre);
    }

    private void verifyBookExistsInDatabase(Book savedBook) {
        Book foundBook = mongoTemplate.findById(savedBook.getId(), Book.class);
        assertThat(foundBook)
                .as("Book should exist in database after saving")
                .isNotNull();
        assertBooksAreEqual(foundBook, savedBook);
    }

    private void assertBooksAreEqual(Book actual, Book expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
        assertThat(actual.getAuthor().getId()).isEqualTo(expected.getAuthor().getId());

        List<String> actualGenreIds = actual.getGenres().stream()
                .map(Genre::getId)
                .toList();
        List<String> expectedGenreIds = expected.getGenres().stream()
                .map(Genre::getId)
                .toList();

        assertThat(actualGenreIds)
                .containsExactlyInAnyOrderElementsOf(expectedGenreIds);
    }

    private void assertBookHasCompleteData(Book book) {
        assertThat(book.getTitle())
                .as("Book title should not be null or empty")
                .isNotNull()
                .isNotBlank();

        assertThat(book.getAuthor())
                .as("Book author should not be null")
                .isNotNull()
                .satisfies(author -> assertThat(author.getId()).isNotNull());

        assertThat(book.getGenres())
                .as("Book genres should not be null or empty")
                .isNotNull()
                .isNotEmpty()
                .allSatisfy(genre -> assertThat(genre.getId()).isNotNull());
    }
}