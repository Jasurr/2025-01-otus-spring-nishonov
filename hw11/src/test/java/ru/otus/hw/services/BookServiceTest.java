package ru.otus.hw.services;

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
import ru.otus.hw.config.TestMongockConfig;
import ru.otus.hw.dto.BookDto;
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
    private ReactiveMongoTemplate reactiveMongoTemplate;

    private Author author;
    private Genre genre;

    @BeforeEach
    @DisplayName("Setup common test data")
    void setUp() {
        Mono<Author> authorMono = reactiveMongoTemplate.findOne(
                Query.query(Criteria.where("fullName").is(TEST_AUTHOR_NAME)), Author.class);
        author = authorMono.block();
        assertThat(author)
                .as("Author with name " + TEST_AUTHOR_NAME + " not found")
                .isNotNull();

        Mono<Genre> genreMono = reactiveMongoTemplate.findOne(
                Query.query(Criteria.where("name").is(TEST_GENRE_NAME)), Genre.class);
        genre = genreMono.block();
        assertThat(genre)
                .as("Genre with name " + TEST_GENRE_NAME + " not found")
                .isNotNull();
    }

    @Test
    @DisplayName("Find book by ID should return correct book")
    void shouldFindBookById() {
        Mono<BookDto> savedBookMono = bookService.insert(TEST_BOOK_TITLE, author.getId(), Set.of(genre.getId()));

        StepVerifier.create(savedBookMono)
                .assertNext(savedBook -> bookService.findById(savedBook.id())
                        .as(StepVerifier::create)
                        .assertNext(foundBook -> assertThat(foundBook)
                                .hasFieldOrPropertyWithValue("title", TEST_BOOK_TITLE)
                                .hasFieldOrPropertyWithValue("author.id", author.getId())
                                .satisfies(book -> assertThat(book.genres()).anyMatch(g -> g.id().equals(genre.getId()))))
                        .verifyComplete())
                .verifyComplete();
    }

    @Test
    @DisplayName("Find all books should return non-empty flux")
    void shouldFindAllBooks() {
        StepVerifier.create(bookService.findAll().collectList())
                .assertNext(books -> assertThat(books)
                        .isNotEmpty()
                        .allMatch(book -> book.author() != null && book.genres() != null))
                .verifyComplete();
    }

    @Test
    @DisplayName("Insert book should create a new book")
    void shouldInsertBook() {
        StepVerifier.create(bookService.insert(TEST_BOOK_TITLE, author.getId(), Set.of(genre.getId())))
                .assertNext(insertedBook -> assertThat(insertedBook)
                        .isNotNull()
                        .hasFieldOrPropertyWithValue("title", TEST_BOOK_TITLE)
                        .hasFieldOrPropertyWithValue("author.id", author.getId())
                        .satisfies(book -> assertThat(book.genres()).anyMatch(g -> g.id().equals(genre.getId()))))
                .verifyComplete();
    }

    @Test
    @DisplayName("Update book should modify book title")
    void shouldUpdateBook() {
        Mono<BookDto> insertedBookMono = bookService.insert(TEST_BOOK_TITLE, author.getId(), Set.of(genre.getId()));

        StepVerifier.create(insertedBookMono)
                .assertNext(insertedBook -> bookService.update(insertedBook.id(), UPDATED_BOOK_TITLE, author.getId(), Set.of(genre.getId()))
                        .as(StepVerifier::create)
                        .assertNext(updatedBook -> assertThat(updatedBook)
                                .isNotNull()
                                .hasFieldOrPropertyWithValue("title", UPDATED_BOOK_TITLE)
                                .hasFieldOrPropertyWithValue("author.id", author.getId())
                                .satisfies(book -> assertThat(book.genres()).anyMatch(g -> g.id().equals(genre.getId()))))
                        .verifyComplete())
                .verifyComplete();
    }

    @Test
    @DisplayName("Delete book should remove book by ID")
    void shouldDeleteBook() {
        Mono<BookDto> insertedBookMono = bookService.insert(TEST_BOOK_TITLE, author.getId(), Set.of(genre.getId()));

        StepVerifier.create(insertedBookMono)
                .assertNext(insertedBook -> {
                    bookService.deleteById(insertedBook.id())
                            .as(StepVerifier::create)
                            .verifyComplete();
                    bookService.findById(insertedBook.id())
                            .as(StepVerifier::create)
                            .expectNextCount(0)
                            .verifyComplete();
                })
                .verifyComplete();
    }
}