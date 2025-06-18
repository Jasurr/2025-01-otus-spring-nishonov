package ru.otus.hw.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.mockito.BDDMockito.given;

@WebFluxTest(AuthorRestController.class)
class AuthorRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AuthorService authorService;

    @Test
    void testGetAllAuthors() {
        List<AuthorDto> authors = List.of(
                new AuthorDto("68498f529934676eff037aa1", "Author One"),
                new AuthorDto("68498f529934676eff037aa2", "Author Two")
        );
        given(authorService.findAll()).willReturn(Flux.fromIterable(authors));

        webTestClient.get()
                .uri("/api/v1/authors")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(AuthorDto.class)
                .value(list -> {
                    assert list.get(0).id().equals("68498f529934676eff037aa1");
                    assert list.get(0).fullName().equals("Author One");
                    assert list.get(1).id().equals("68498f529934676eff037aa2");
                    assert list.get(1).fullName().equals("Author Two");
                });
    }
}