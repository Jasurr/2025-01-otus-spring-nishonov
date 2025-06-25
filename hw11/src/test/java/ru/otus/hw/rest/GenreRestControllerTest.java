package ru.otus.hw.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.BDDMockito.given;

@WebFluxTest(GenreRestController.class)
@ContextConfiguration(classes = GenreRestController.class) // Limit context to the controller
class GenreRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GenreService genreService;

    @Test
    void testGetAllGenres() {
        List<GenreDto> genres = List.of(
                new GenreDto("1", "Fiction"),
                new GenreDto("2", "Non-Fiction")
        );
        given(genreService.findAll()).willReturn(Flux.fromIterable(genres));

        webTestClient.get()
                .uri("/api/v1/genres")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(GenreDto.class)
                .hasSize(2)
                .value(list -> {
                    assert list.get(0).id().equals("1");
                    assert list.get(0).name().equals("Fiction");
                    assert list.get(1).id().equals("2");
                    assert list.get(1).name().equals("Non-Fiction");
                });
    }

    @Test
    void testGetAllGenresEmptyList() {
        given(genreService.findAll()).willReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/v1/genres")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(GenreDto.class)
                .hasSize(0);
    }
}