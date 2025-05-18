package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class BookRepositoryTest {
    private static final String FIRST_AUTHOR_ID = "1";
    private static final String FIRST_GENRE_ID = "1";
    private static final String TEST_BOOK_TITLE = "Test Book";

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Author author;
    private Genre genre;

    @BeforeEach
    void setUp() {
        author = mongoTemplate.findById(FIRST_AUTHOR_ID, Author.class);
        genre = mongoTemplate.findById(FIRST_GENRE_ID, Genre.class);
    }

    @Test
    @DisplayName("Should save a book successfully")
    void shouldSaveBook() {
        Book book = createTestBook();
        Book savedBook = bookRepository.save(book);
        assertEquals(TEST_BOOK_TITLE, savedBook.getTitle());
        assertEquals(author.getId(), savedBook.getAuthorId());
        assertTrue(savedBook.getGenreIds().contains(genre.getId()));
    }

    @Test
    @DisplayName("Should find a book by ID")
    void shouldFindById() {
        Book savedBook = bookRepository.save(createTestBook());
        Optional<Book> foundBook = bookRepository.findById(savedBook.getId());
        assertTrue(foundBook.isPresent());
        assertEquals(savedBook.getId(), foundBook.get().getId());
    }

    @Test
    @DisplayName("Should find all books with genres")
    void shouldFindAllWithGenres() {
        bookRepository.save(createTestBook());
        List<Book> books = bookRepository.findAll();
        assertThat(books)
                .isNotEmpty()
                .allMatch(b -> b.getTitle() != null && !b.getTitle().isEmpty())
                .allMatch(b -> b.getAuthorId() != null && !b.getAuthorId().isEmpty())
                .allMatch(b -> !b.getGenreIds().isEmpty());
    }

    @Test
    @DisplayName("Should delete a book by ID")
    void shouldDeleteById() {
        Book savedBook = bookRepository.save(createTestBook());
        bookRepository.deleteById(savedBook.getId());
        assertFalse(bookRepository.findById(savedBook.getId()).isPresent());
    }

    private Book createTestBook() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthorId(author.getId());
        book.setGenreIds(List.of(genre.getId()));
        return book;
    }
}
