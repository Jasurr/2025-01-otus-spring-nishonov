package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
class BookRepositoryTest {
    private static final long FIRST_AUTHOR_ID = 1L;
    private static final long FIRST_GENRE_ID = 1L;
    private static final String TEST_BOOK_TITLE = "Test Book";

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager em;

    private Author author;
    private Genre genre;

    @BeforeEach
    void setUp() {
        author = em.find(Author.class, FIRST_AUTHOR_ID);
        genre = em.find(Genre.class, FIRST_GENRE_ID);
    }

    @Test
    @DisplayName("Should save a book successfully")
    void shouldSaveBook() {
        Book book = createTestBook();
        Book savedBook = bookRepository.save(book);
        assertEquals(TEST_BOOK_TITLE, savedBook.getTitle());
        assertEquals(author, savedBook.getAuthor());
        assertTrue(savedBook.getGenres().contains(genre));
    }

    @Test
    @DisplayName("Should find a book by ID")
    void shouldFindById() {
        Book savedBook = bookRepository.save(createTestBook());
        em.flush();
        Optional<Book> foundBook = bookRepository.findById(savedBook.getId());
        assertTrue(foundBook.isPresent());
        assertEquals(savedBook.getId(), foundBook.get().getId());
    }

    @Test
    @DisplayName("Should find all books with genres")
    void shouldFindAllWithGenres() {
        bookRepository.save(createTestBook());
        em.flush();
        List<Book> books = bookRepository.findAll();
        assertThat(books)
                .isNotEmpty()
                .allMatch(b -> b.getTitle() != null && !b.getTitle().isEmpty())
                .allMatch(b -> b.getAuthor() != null && !b.getAuthor().getFullName().isEmpty())
                .allMatch(b -> !b.getGenres().isEmpty());
    }

    @Test
    @DisplayName("Should delete a book by ID")
    void shouldDeleteById() {
        Book savedBook = bookRepository.save(createTestBook());
        bookRepository.deleteById(savedBook.getId());
        em.flush();
        assertFalse(bookRepository.findById(savedBook.getId()).isPresent());
    }

    private Book createTestBook() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor(author);
        book.setGenres(List.of(genre));
        return book;
    }
}