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

    private static final String TEST_BOOK_TITLE = "Test Book";

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
    @DisplayName("Should save a book successfully")
    void shouldSaveBook() {
        Book book = createTestBook();
        Book savedBook = bookRepository.save(book);

        assertThat(savedBook.getId()).isNotNull().isNotBlank();
        assertThat(savedBook.getTitle()).isEqualTo(TEST_BOOK_TITLE);
        assertThat(savedBook.getAuthor()).isEqualTo(author);
        assertThat(savedBook.getGenres()).containsExactly(genre);
    }

    @Test
    @DisplayName("Should find a book by ID")
    void shouldFindById() {
        Book savedBook = bookRepository.save(createTestBook());
        Optional<Book> foundBook = bookRepository.findById(savedBook.getId());

        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getId()).isEqualTo(savedBook.getId());
        assertThat(foundBook.get().getTitle()).isEqualTo(savedBook.getTitle());
        assertThat(foundBook.get().getAuthor().getId()).isEqualTo(savedBook.getAuthor().getId());

        // Compare genre IDs to avoid object equality issues
        List<String> savedGenreIds = savedBook.getGenres().stream().map(Genre::getId).toList();
        List<String> foundGenreIds = foundBook.get().getGenres().stream().map(Genre::getId).toList();
        assertThat(foundGenreIds).containsExactlyInAnyOrderElementsOf(savedGenreIds);
    }

    @Test
    @DisplayName("Should find all books with related data (author and genres)")
    void shouldFindAll() {
        bookRepository.save(createTestBook());
        var book = new Book(null, TEST_BOOK_TITLE + " 2", author, List.of(genre));
        bookRepository.save(book);

        List<Book> books = bookRepository.findAll();

        assertThat(books)
                .isNotEmpty()
                .hasSizeGreaterThanOrEqualTo(2)
                .allMatch(b -> b.getTitle() != null && !b.getTitle().isEmpty())
                .allMatch(b -> b.getAuthor() != null && b.getAuthor().getId() != null)
                .allMatch(b -> b.getGenres() != null && !b.getGenres().isEmpty());
    }

    @Test
    @DisplayName("Should delete a book by ID")
    void shouldDeleteById() {
        Book savedBook = bookRepository.save(createTestBook());
        bookRepository.deleteById(savedBook.getId());

        assertThat(bookRepository.findById(savedBook.getId())).isEmpty();
    }

    private Book createTestBook() {
        Book book = new Book();
        book.setTitle(TEST_BOOK_TITLE);
        book.setAuthor(author);
        book.setGenres(List.of(genre));
        return book;
    }
}