package ru.otus.hw.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.given;

@WebFluxTest(BookRestController.class)
@ContextConfiguration(classes = BookRestController.class) // Limit context to the controller
class BookRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BookService bookService;

    private final AuthorDto author = new AuthorDto("1", "Author");
    private final GenreDto genre = new GenreDto("1", "Genre");
    private final BookDto book = new BookDto("10", "Book Title", author, List.of(genre));

    @Test
    void testGetAllBooks() {
        given(bookService.findAll()).willReturn(Flux.just(book));

        webTestClient.get()
                .uri("/api/v1/books")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(BookDto.class)
                .hasSize(1)
                .value(list -> {
                    assert list.get(0).id().equals("10");
                    assert list.get(0).title().equals("Book Title");
                    assert list.get(0).author().id().equals("1");
                    assert list.get(0).author().fullName().equals("Author");
                    assert list.get(0).genres().get(0).id().equals("1");
                    assert list.get(0).genres().get(0).name().equals("Genre");
                });
    }

    @Test
    void testGetBookById() {
        given(bookService.findById("10")).willReturn(Mono.just(book));

        webTestClient.get()
                .uri("/api/v1/books/10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BookDto.class)
                .value(bookDto -> {
                    assert bookDto.id().equals("10");
                    assert bookDto.title().equals("Book Title");
                    assert bookDto.author().id().equals("1");
                    assert bookDto.author().fullName().equals("Author");
                    assert bookDto.genres().get(0).id().equals("1");
                    assert bookDto.genres().get(0).name().equals("Genre");
                });
    }

    @Test
    void testInsertBook() {
        given(bookService.insert("Book Title", "1", Set.of("1")))
                .willReturn(Mono.just(book));

        webTestClient.post()
                .uri("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new BookDto(null, "Book Title", new AuthorDto("1", "Author"), List.of(new GenreDto("1", "Genre"))))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BookDto.class)
                .value(bookDto -> {
                    assert bookDto.id().equals("10");
                    assert bookDto.title().equals("Book Title");
                    assert bookDto.author().id().equals("1");
                    assert bookDto.author().fullName().equals("Author");
                    assert bookDto.genres().get(0).id().equals("1");
                    assert bookDto.genres().get(0).name().equals("Genre");
                });
    }

    @Test
    void testUpdateBook() {
        given(bookService.update("10", "Book Title", "1", Set.of("1")))
                .willReturn(Mono.just(book));

        webTestClient.put()
                .uri("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new BookDto("10", "Book Title", new AuthorDto("1", "Author"), List.of(new GenreDto("1", "Genre"))))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BookDto.class)
                .value(bookDto -> {
                    assert bookDto.id().equals("10");
                    assert bookDto.title().equals("Book Title");
                    assert bookDto.author().id().equals("1");
                    assert bookDto.author().fullName().equals("Author");
                    assert bookDto.genres().get(0).id().equals("1");
                    assert bookDto.genres().get(0).name().equals("Genre");
                });
    }

    @Test
    void testDeleteBook() {
        given(bookService.deleteById("10")).willReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/books/10")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }
}