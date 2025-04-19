package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Book Service Tests")
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private GenreService genreService;

    private Author author;
    private Genre genre;

    @BeforeEach
    @DisplayName("Setup common test data")
    void setUp() {
        // Preload common test data
        author = authorService.findAll().get(0);
        genre = genreService.findAll().get(0);
    }

    // Helper method: Create a test book
    private Book createTestBook() {
        var book = new Book();
        book.setTitle("Test Book");
        book.setAuthor(author);
        book.setGenres(List.of(genre));
        return book;
    }

    @Test
    @DisplayName("Find book by ID should return correct book")
    void shouldFindBookById() {
        var savedBook = bookService.insert("Test Book", author.getId(), Set.of(genre.getId()));
        var foundBook = bookService.findById(savedBook.getId());

        assertNotNull(foundBook.orElse(null));
        assertEquals("Test Book", foundBook.get().getTitle());
        assertEquals(author.getId(), foundBook.get().getAuthor().getId());
    }

    @Test
    @DisplayName("Find all books should return non-empty list")
    void shouldFindAllBooks() {
        var books = bookService.findAll();
        assertFalse(books.isEmpty());
    }

    @Test
    @DisplayName("Insert book should create a new book")
    void shouldInsertBook() {
        var insertedBook = bookService.insert("Test Book", author.getId(), Set.of(genre.getId()));
        assertNotNull(insertedBook.getId());
        assertEquals("Test Book", insertedBook.getTitle());
    }

    @Test
    @DisplayName("Update book should modify book title")
    void shouldUpdateBook() {
        var insertedBook = bookService.insert("Test Book", author.getId(), Set.of(genre.getId()));
        var updatedBook = bookService.update(insertedBook.getId(), "Updated Book", author.getId(), Set.of(genre.getId()));
        assertEquals("Updated Book", updatedBook.getTitle());
    }

    @Test
    @DisplayName("Delete book should remove book by ID")
    void shouldDeleteBook() {
        var insertedBook = bookService.insert("Test Book", author.getId(), Set.of(genre.getId()));
        bookService.deleteById(insertedBook.getId());
        var foundBook = bookService.findById(insertedBook.getId());
        assertFalse(foundBook.isPresent());
    }
}