package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.migrate.InitialTestDataMigration;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@DisplayName("Book Repository Tests for MongoDB (Reactive)")
@Import(InitialTestDataMigration.class)
class BookRepositoryTest {

    private static final String TEST_BOOK_TITLE = "Sample Book";
    private static final String TEST_BOOK_TITLE_2 = "Sample Book 2";
    private static final String TEST_AUTHOR_NAME = "Author 1";
    private static final String TEST_GENRE_NAME = "Genre 1";

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    private Author testAuthor;
    private Genre testGenre;

    @BeforeEach
    void setUp() {
        reactiveMongoTemplate.remove(Query.query(Criteria.where("title").regex("^" + TEST_BOOK_TITLE)), Book.class)
                .as(StepVerifier::create)
                .expectNextMatches(result -> result.getDeletedCount() >= 0) // Accept any delete count
                .verifyComplete();

        testAuthor = findAuthorByName(TEST_AUTHOR_NAME);
        testGenre = findGenreByName(TEST_GENRE_NAME);
    }

    @Test
    @DisplayName("Should save a book successfully")
    void shouldSaveBook() {
        // Given
        Book book = createTestBook(TEST_BOOK_TITLE);

        // When
        Mono<Book> savedBookMono = bookRepository.save(book);

        // Then
        StepVerifier.create(savedBookMono)
                .assertNext(savedBook -> assertBookIsSavedCorrectly(savedBook, TEST_BOOK_TITLE))
                .verifyComplete();

        // Verify book exists in database
        verifyBookExistsInDatabase(book);
    }

    @Test
    @DisplayName("Should find a book by ID")
    void shouldFindById() {
        Book testBook = createTestBook(TEST_BOOK_TITLE);
        Book savedBook = reactiveMongoTemplate.save(testBook).block();
        StepVerifier.create(bookRepository.findById(savedBook.getId()))
                .assertNext(foundBook -> assertBooksAreEqual(foundBook, savedBook))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find all books with complete data")
    void shouldFindAll() {
        // Given - use ReactiveMongoTemplate to set up test data
        Book book1 = createTestBook(TEST_BOOK_TITLE);
        Book book2 = createTestBook(TEST_BOOK_TITLE_2);

        reactiveMongoTemplate.save(book1)
                .then(reactiveMongoTemplate.save(book2))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();

        // When - test the repository method
        StepVerifier.create(bookRepository.findAll().collectList())
                .assertNext(allBooks -> {
                    assertThat(allBooks)
                            .hasSizeGreaterThanOrEqualTo(2)
                            .allSatisfy(this::assertBookHasCompleteData)
                            .extracting(Book::getTitle)
                            .contains(TEST_BOOK_TITLE, TEST_BOOK_TITLE_2);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should delete a book by ID")
    void shouldDeleteById() {
        // Given - use ReactiveMongoTemplate to set up test data
        Book testBook = createTestBook(TEST_BOOK_TITLE);
        Book savedBook = reactiveMongoTemplate.save(testBook).block();
        assertThat(savedBook.getId()).isNotNull();
        System.out.println("Deleting book with ID: " + savedBook.getId());

        // When - test the repository delete method
        StepVerifier.create(bookRepository.deleteById(savedBook.getId()))
                .verifyComplete();

        // Then - verify the book no longer exists
        Mono<Book> notExistsBook = reactiveMongoTemplate.findOne(
                Query.query(Criteria.where("_id").is(savedBook.getId())), Book.class);
        StepVerifier.create(notExistsBook)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle non-existent book ID gracefully")
    void shouldHandleNonExistentId() {
        // Given
        String nonExistentId = "507f1f77bcf86cd799439011";

        // When & Then - test repository method with non-existent ID
        StepVerifier.create(bookRepository.findById(nonExistentId))
                .expectNextCount(0)
                .verifyComplete();

        // Verify using ReactiveMongoTemplate
        StepVerifier.create(reactiveMongoTemplate.findById(nonExistentId, Book.class))
                .expectNextCount(0)
                .verifyComplete();
    }

    // Helper methods
    private Author findAuthorByName(String name) {
        Mono<Author> authorMono = reactiveMongoTemplate.findOne(
                Query.query(Criteria.where("fullName").is(name)), Author.class);
        Author author = authorMono.block(); // Blocking in tests is acceptable
        assertThat(author)
                .as("Author with name '%s' should exist in test data", name)
                .isNotNull();
        return author;
    }

    private Genre findGenreByName(String name) {
        Mono<Genre> genreMono = reactiveMongoTemplate.findOne(
                Query.query(Criteria.where("name").is(name)), Genre.class);
        Genre genre = genreMono.block(); // Blocking in tests is acceptable
        assertThat(genre)
                .as("Genre with name '%s' should exist in test data", name)
                .isNotNull();
        return genre;
    }

    private Book createTestBook(String title) {
        return new Book(null, title, testAuthor, List.of(testGenre));
    }

    private void assertBookIsSavedCorrectly(Book savedBook, String expectedTitle) {
        assertThat(savedBook.getId())
                .as("Saved book should have a generated ID")
                .isNotNull()
                .isNotBlank();

        assertThat(savedBook.getTitle())
                .as("Saved book should have correct title")
                .isEqualTo(expectedTitle);

        assertThat(savedBook.getAuthor())
                .as("Saved book should have correct author")
                .isEqualTo(testAuthor);

        assertThat(savedBook.getGenres())
                .as("Saved book should have correct genres")
                .containsExactly(testGenre);
    }

    private void verifyBookExistsInDatabase(Book book) {
        StepVerifier.create(reactiveMongoTemplate.findOne(
                        Query.query(Criteria.where("title").is(book.getTitle())), Book.class))
                .assertNext(foundBook -> assertBooksAreEqual(foundBook, book))
                .verifyComplete();
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