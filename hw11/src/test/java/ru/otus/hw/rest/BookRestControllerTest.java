package ru.otus.hw.rest;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.BookService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;

@WebFluxTest(BookRestController.class)
class BookRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BookService bookService;

    private final AuthorDto author = new AuthorDto("1", "Author");
    private final GenreDto genre = new GenreDto("1", "Genre");
    private final BookDto book = new BookDto("10", "Book Title", author, List.of(genre));

    @Test
    void shouldReturnAllBooks() {
        Mockito.when(bookService.findAll()).thenReturn(Flux.just(book));

        webTestClient.get()
                .uri("/api/v1/books")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDto.class)
                .hasSize(1)
                .contains(book);
    }

    @Test
    void shouldReturnBookById() {
        Mockito.when(bookService.findById("10")).thenReturn(Mono.just(book));

        webTestClient.get()
                .uri("/api/v1/books/10")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .isEqualTo(book);
    }

    @Test
    void shouldInsertBook() {
        Mockito.when(bookService.insert(anyString(), anyString(), anySet()))
                .thenReturn(Mono.just(book));

        webTestClient.post()
                .uri("/api/v1/books/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(book)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .isEqualTo(book);
    }

    @Test
    void shouldUpdateBook() {
        Mockito.when(bookService.update(anyString(), anyString(), anyString(), anySet()))
                .thenReturn(Mono.just(book));

        webTestClient.put()
                .uri("/api/v1/books/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(book)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .isEqualTo(book);
    }

    @Test
    void shouldDeleteBook() {
        Mockito.when(bookService.deleteById("10")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/books/delete/10")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }
}
