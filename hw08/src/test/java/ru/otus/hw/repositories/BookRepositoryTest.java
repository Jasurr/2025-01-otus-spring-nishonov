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

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import(TestMongockConfig.class)
@DisplayName("Book Repository Tests for MongoDB")
class BookRepositoryTest {

    private static final String TEST_BOOK_TITLE = "Sample Book";

    private static final String TEST_AUTHOR_NAME = "Author 1";

    private static final String TEST_GENRE_NAME = "Genre 1";

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Author author;

    private Genre genre;

    @BeforeEach
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
    @DisplayName("Should save a book successfully using MongoTemplate")
    void shouldSaveBook() {
        Book book = createTestBook();
        Book savedBook = bookRepository.save(book);

        assertThat(savedBook.getId()).isNotNull().isNotBlank();
        assertThat(savedBook.getTitle()).isEqualTo(TEST_BOOK_TITLE);
        assertThat(savedBook.getAuthor()).isEqualTo(author);
        assertThat(savedBook.getGenres()).containsExactly(genre);

        Book foundBook = mongoTemplate.findById(savedBook.getId(), Book.class);
        assertThat(foundBook).isNotNull();
        assertThat(foundBook.getId()).isEqualTo(savedBook.getId());
        assertThat(foundBook.getTitle()).isEqualTo(savedBook.getTitle());
        assertThat(foundBook.getAuthor().getId()).isEqualTo(savedBook.getAuthor().getId());
        List<String> savedGenreIds = savedBook.getGenres().stream().map(Genre::getId).toList();
        List<String> foundGenreIds = foundBook.getGenres().stream().map(Genre::getId).toList();
        assertThat(foundGenreIds).containsExactlyInAnyOrderElementsOf(savedGenreIds);
    }

    @Test
    @DisplayName("Should find a book by ID using MongoTemplate")
    void shouldFindById() {
        Book existingBook = mongoTemplate.findOne(
                Query.query(Criteria.where("title").is(TEST_BOOK_TITLE)), Book.class);
        assertThat(existingBook)
                .as("Book with title " + TEST_BOOK_TITLE + " not found in the database")
                .isNotNull();

        Book foundBook = mongoTemplate.findById(existingBook.getId(), Book.class);

        assertThat(foundBook).isNotNull();
        assertThat(foundBook.getId()).isEqualTo(existingBook.getId());
        assertThat(foundBook.getTitle()).isEqualTo(existingBook.getTitle());
        assertThat(foundBook.getAuthor().getId()).isEqualTo(existingBook.getAuthor().getId());

        List<String> existingGenreIds = existingBook.getGenres().stream().map(Genre::getId).toList();
        List<String> foundGenreIds = foundBook.getGenres().stream().map(Genre::getId).toList();
        assertThat(foundGenreIds).containsExactlyInAnyOrderElementsOf(existingGenreIds);
    }

    @Test
    @DisplayName("Should find all books with related data using MongoTemplate")
    void shouldFindAll() {
        Book book1 = createTestBook();
        Book book2 = new Book(null, TEST_BOOK_TITLE + " 2", author, List.of(genre));
        mongoTemplate.save(book1);
        mongoTemplate.save(book2);

        List<Book> books = mongoTemplate.findAll(Book.class);

        assertThat(books)
                .isNotEmpty()
                .hasSizeGreaterThanOrEqualTo(2)
                .allMatch(b -> b.getTitle() != null && !b.getTitle().isEmpty())
                .allMatch(b -> b.getAuthor() != null && b.getAuthor().getId() != null)
                .allMatch(b -> b.getGenres() != null && !b.getGenres().isEmpty());
    }

    @Test
    @DisplayName("Should delete a book by ID using MongoTemplate")
    void shouldDeleteById() {
        var book = mongoTemplate.findOne(
                Query.query(Criteria.where("title").is(TEST_BOOK_TITLE)), Book.class);
        assertThat(book).isNotNull();
        bookRepository.deleteById(book.getId());

        assertThat(bookRepository.findById(book.getId())).isEmpty();
    }

    private Book createTestBook() {
        Book book = new Book();
        book.setTitle(TEST_BOOK_TITLE);
        book.setAuthor(author);
        book.setGenres(List.of(genre));
        return book;
    }
}