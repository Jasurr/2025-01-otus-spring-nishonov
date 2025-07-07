package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.migrate.InitialTestDataMigration;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import({BookServiceImpl.class, BookMapper.class, InitialTestDataMigration.class})
@DisplayName("Book Service Tests for MongoDB")
class BookServiceTest {

    private static final String TEST_BOOK_TITLE = "Test Book";
    private static final String UPDATED_BOOK_TITLE = "Updated Book";
    private static final String TEST_AUTHOR_NAME = "Author 1";
    private static final String TEST_GENRE_NAME = "Genre 1";

    @Autowired
    private BookService bookService;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    private Author author;
    private Genre genre;

    @BeforeEach
    @DisplayName("Setup common test data")
    void setUp() {
        // Insert test data directly
        Author testAuthor = new Author(null, TEST_AUTHOR_NAME);
        Genre testGenre = new Genre(null, TEST_GENRE_NAME);

        author = reactiveMongoTemplate.save(testAuthor).block();
        genre = reactiveMongoTemplate.save(testGenre).block();

        assertThat(author)
                .as("Failed to create author with name " + TEST_AUTHOR_NAME)
                .isNotNull()
                .extracting(Author::getId)
                .isNotNull();

        assertThat(genre)
                .as("Failed to create genre with name " + TEST_GENRE_NAME)
                .isNotNull()
                .extracting(Genre::getId)
                .isNotNull();
    }

    @Test
    @DisplayName("Find book by ID should return correct book")
    void shouldFindBookById() {
        // Chain operations properly using flatMap instead of nested StepVerifiers
        Mono<BookDto> result = bookService.insert(TEST_BOOK_TITLE, author.getId(), Set.of(genre.getId()))
                .flatMap(savedBook -> bookService.findById(savedBook.id()));

        StepVerifier.create(result)
                .assertNext(foundBook -> {
                    assertThat(foundBook)
                            .isNotNull()
                            .hasFieldOrPropertyWithValue("title", TEST_BOOK_TITLE);

                    assertThat(foundBook.author())
                            .isNotNull()
                            .hasFieldOrPropertyWithValue("id", author.getId());

                    assertThat(foundBook.genres())
                            .hasSize(1)
                            .anyMatch(g -> g.id().equals(genre.getId()));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Find all books should return non-empty flux")
    void shouldFindAllBooks() {
        // Insert a book first, then find all
        Mono<java.util.List<BookDto>> result = bookService.insert(TEST_BOOK_TITLE, author.getId(), Set.of(genre.getId()))
                .then(bookService.findAll().collectList());

        StepVerifier.create(result)
                .assertNext(books -> {
                    assertThat(books)
                            .isNotEmpty()
                            .allMatch(book -> book.author() != null && book.genres() != null);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Insert book should create a new book")
    void shouldInsertBook() {
        StepVerifier.create(bookService.insert(TEST_BOOK_TITLE, author.getId(), Set.of(genre.getId())))
                .assertNext(insertedBook -> {
                    assertThat(insertedBook)
                            .isNotNull()
                            .hasFieldOrPropertyWithValue("title", TEST_BOOK_TITLE);

                    assertThat(insertedBook.author())
                            .isNotNull()
                            .hasFieldOrPropertyWithValue("id", author.getId());

                    assertThat(insertedBook.genres())
                            .hasSize(1)
                            .anyMatch(g -> g.id().equals(genre.getId()));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Update book should modify book title")
    void shouldUpdateBook() {
        // Chain insert and update operations properly
        Mono<BookDto> result = bookService.insert(TEST_BOOK_TITLE, author.getId(), Set.of(genre.getId()))
                .flatMap(insertedBook -> bookService.update(
                        insertedBook.id(),
                        UPDATED_BOOK_TITLE,
                        author.getId(),
                        Set.of(genre.getId())
                ));

        StepVerifier.create(result)
                .assertNext(updatedBook -> {
                    assertThat(updatedBook)
                            .isNotNull()
                            .hasFieldOrPropertyWithValue("title", UPDATED_BOOK_TITLE);

                    assertThat(updatedBook.author())
                            .isNotNull()
                            .hasFieldOrPropertyWithValue("id", author.getId());

                    assertThat(updatedBook.genres())
                            .hasSize(1)
                            .anyMatch(g -> g.id().equals(genre.getId()));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Delete book should remove book by ID")
    void shouldDeleteBook() {
        // Chain insert, delete, and verification operations
        Mono<BookDto> deleteAndVerifyResult = bookService.insert(TEST_BOOK_TITLE, author.getId(), Set.of(genre.getId()))
                .flatMap(insertedBook ->
                        bookService.deleteById(insertedBook.id())
                                .then(bookService.findById(insertedBook.id()))
                );

        StepVerifier.create(deleteAndVerifyResult)
                .expectNextCount(0) // Should not find any book after deletion
                .verifyComplete();
    }

    @Test
    @DisplayName("Find book by non-existent ID should return empty or throw exception")
    void shouldHandleFindByNonExistentId() {
        String nonExistentId = "507f1f77bcf86cd799439011"; // Valid ObjectId format

        // Test based on your service's actual behavior - it might return empty or throw exception
        StepVerifier.create(bookService.findById(nonExistentId))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DisplayName("Update non-existent book should throw EntityNotFoundException")
    void shouldThrowExceptionWhenUpdatingNonExistentBook() {
        String nonExistentId = "507f1f77bcf86cd799439011"; // Valid ObjectId format

        StepVerifier.create(bookService.update(nonExistentId, UPDATED_BOOK_TITLE, author.getId(), Set.of(genre.getId())))
                .expectError(ru.otus.hw.exceptions.EntityNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Delete non-existent book should complete without error")
    void shouldHandleDeleteOfNonExistentBook() {
        String nonExistentId = "507f1f77bcf86cd799439011"; // Valid ObjectId format

        StepVerifier.create(bookService.deleteById(nonExistentId))
                .verifyComplete(); // Should complete without error even if book doesn't exist
    }
}