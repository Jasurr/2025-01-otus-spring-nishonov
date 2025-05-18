package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(BookController.class)
class BookControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    private final List<Book> books = List.of(
            new Book(1L, "Book 1", new Author(1L, "Author 1"), List.of(new Genre(1L, "Genre 1"))),
            new Book(2L, "Book 2", new Author(2L, "Author 2"), List.of(new Genre(2L, "Genre 2"))),
            new Book(3L, "Book 3", new Author(3L, "Author 3"), List.of(new Genre(3L, "Genre 3")))
    );

    private final List<Genre> genres = List.of(
            new Genre(1L, "Genre 1"),
            new Genre(2L, "Genre 2"),
            new Genre(3L, "Genre 3")
    );

    private final List<Author> authors = List.of(
            new Author(1L, "Author 1"),
            new Author(2L, "Author 2"),
            new Author(3L, "Author 3")
    );

    @Test
    @DisplayName("should return books page with books")
    void shouldReturnBooksPage() throws Exception {
        when(bookService.findAll()).thenReturn(books);
        mvc.perform(get("/book"))
                .andExpect(view().name("book_list"))
                .andExpect(model().attribute("books", books));
    }

    @Test
    @DisplayName("should return add book page with authors and genres")
    void shouldReturnAddBookPage() throws Exception {
        when(authorService.findAll()).thenReturn(authors);
        when(genreService.findAll()).thenReturn(genres);

        mvc.perform(get("/book/add"))
                .andExpect(view().name("book_add"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("genres"));
    }

    @Test
    @DisplayName("should show add book form with authors and genres")
    void shouldShowAddBookForm() throws Exception {
        List<Author> authors = List.of(new Author(1L, "Author 1"));
        List<Genre> genres = List.of(new Genre(1L, "Genre 1"));

        when(authorService.findAll()).thenReturn(authors);
        when(genreService.findAll()).thenReturn(genres);

        mvc.perform(get("/book/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("book_add"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attribute("authors", authors))
                .andExpect(model().attribute("genres", genres));
    }


    @Test
    @DisplayName("should return edit book page with authors and genres")
    void shouldReturnEditBookPage() throws Exception {
        long bookId = 1L;
        Book book = new Book(bookId, "Book 1", new Author(1L, "Author 1"), List.of(new Genre(1L, "Genre 1")));
        when(bookService.findById(bookId)).thenReturn(Optional.of(book));
        when(authorService.findAll()).thenReturn(authors);
        when(genreService.findAll()).thenReturn(genres);

        mvc.perform(get("/book/edit/" + bookId))
                .andExpect(view().name("book_edit"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("genres"));
    }

    @Test
    @DisplayName("should return delete book page")
    void shouldDeleteBookAndRedirect() throws Exception {
        long bookId = 1L;
        Book book = new Book(bookId, "Book 1", new Author(1L, "Author 1"), List.of(new Genre(1L, "Genre 1")));
        when(bookService.findById(bookId)).thenReturn(Optional.of(book));

        mvc.perform(get("/book/delete/{id}", bookId))
                .andExpect(redirectedUrl("/book"));

        verify(bookService).deleteById(bookId);
    }
}