package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    private Author author;
    private Genre genre;
    private Book testBook;

    @BeforeEach
    void setUp() {
        author = authorRepository.findAll().get(0);
        genre = genreRepository.findAll().get(0);
        testBook = createTestBook();
    }

    @Test
    @DisplayName("Should save a book successfully")
    void shouldSaveBook() {
        Book savedBook = bookRepository.save(testBook);
        assertNotNull(savedBook.getId());
        assertEquals(testBook.getTitle(), savedBook.getTitle());
        assertEquals(author, savedBook.getAuthor());
    }

    @Test
    @DisplayName("Should find a book by ID")
    void shouldFindById() {
        Book savedBook = bookRepository.save(testBook);
        Book foundBook = bookRepository.findById(savedBook.getId()).orElse(null);
        assertNotNull(foundBook);
        assertEquals(savedBook.getId(), foundBook.getId());
    }

    @Test
    @DisplayName("Should find all books with genres")
    void shouldFindAllWithGenres() {
        bookRepository.save(testBook);
        List<Book> books = bookRepository.findAllWithGenres();
        assertFalse(books.isEmpty());
        assertTrue(books.stream().anyMatch(b -> b.getTitle().equals(testBook.getTitle())));
    }

    @Test
    @DisplayName("Should delete a book by ID")
    void shouldDeleteById() {
        Book savedBook = bookRepository.save(testBook);
        bookRepository.deleteById(savedBook.getId());
        Book foundBook = bookRepository.findById(savedBook.getId()).orElse(null);
        assertNull(foundBook);
    }

    private Book createTestBook() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor(author);
        book.setGenres(List.of(genre));
        return book;
    }
}