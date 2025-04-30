package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(JpaBookRepository.class)
@DisplayName("JpaBookRepository Test")
class JpaBookRepositoryTest {

    @Autowired
    private JpaBookRepository jpaBookRepository;

    @Autowired
    private TestEntityManager em;

    private Author createAndPersistAuthor() {
        var author = new Author();
        author.setFullName("Test Author");
        em.persist(author);
        return author;
    }

    private Genre createAndPersistGenre() {
        var genre = new Genre();
        genre.setName("Test Genre");
        em.persist(genre);
        return genre;
    }

    private Book createBook(String title, Author author, Genre genre) {
        var book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenres(List.of(genre));
        return book;
    }

    @Test
    @DisplayName("Should find a book by ID")
    void shouldFindById() {
        var author = createAndPersistAuthor();
        var genre = createAndPersistGenre();
        var book = createBook("Test Book", author, genre);
        em.persist(book);
        em.flush();

        var optionalBook = jpaBookRepository.findById(book.getId());
        assertThat(optionalBook).isPresent().get()
                .usingRecursiveComparison().isEqualTo(book);
    }

    @Test
    @DisplayName("Should save a book successfully")
    void shouldSaveBook() {
        var author = createAndPersistAuthor();
        var genre = createAndPersistGenre();
        var book = createBook("New Book", author, genre);
        var savedBook = jpaBookRepository.save(book);

        assertNotNull(savedBook.getId());
        assertEquals(book.getTitle(), savedBook.getTitle());
        assertEquals(book.getAuthor(), savedBook.getAuthor());
        assertEquals(book.getGenres(), savedBook.getGenres());
    }

    @Test
    @DisplayName("Should find all books with genres")
    void shouldFindAllWithGenres() {
        var author = createAndPersistAuthor();
        var genre = createAndPersistGenre();
        var book = createBook("Test Book", author, genre);
        em.persist(book);
        em.flush();

        var books = jpaBookRepository.findAllWithGenres();
        assertThat(books)
                .hasSizeGreaterThan(0)
                .allMatch(b -> b.getTitle() != null && !b.getTitle().isEmpty())
                .allMatch(b -> b.getAuthor() != null)
                .allMatch(b -> b.getGenres() != null && !b.getGenres().isEmpty());
    }

    @Test
    @DisplayName("Should delete a book by ID")
    void shouldDeleteById() {
        var author = createAndPersistAuthor();
        var genre = createAndPersistGenre();
        var book = createBook("Test Book", author, genre);
        em.persist(book);
        em.flush();

        jpaBookRepository.deleteById(book.getId());
        em.flush();
        em.clear();
        var deletedBook = em.find(Book.class, book.getId());
        assertNull(deletedBook);
    }
}