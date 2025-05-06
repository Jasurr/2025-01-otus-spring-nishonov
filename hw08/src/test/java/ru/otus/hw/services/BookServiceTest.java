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
@DisplayName("Тесты сервиса книг")
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
    @DisplayName("Подготовка общих тестовых данных")
    void setUp() {
        // Загружаем общие тестовые данные
        author = authorService.findAll().get(0);
        genre = genreService.findAll().get(0);
    }


    @Test
    @DisplayName("Поиск книги по ID должен вернуть правильную книгу")
    void shouldFindBookById() {
        var savedBook = bookService.insert("Тестовая книга", author.getId(), Set.of(genre.getId()));
        var foundBook = bookService.findById(savedBook.getId());

        assertNotNull(foundBook.orElse(null));
        assertEquals("Тестовая книга", foundBook.get().getTitle());
    }

}