package ru.otus.hw.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@WebFluxTest(CommentRestController.class)
class CommentRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CommentService commentService;

    @Test
    void testGetCommentFound() {
        CommentDto comment = new CommentDto("1", "Great book!");
        given(commentService.findById("1")).willReturn(Mono.just(comment));

        webTestClient.get()
                .uri("/api/v1/book/comments/one/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CommentDto.class)
                .value(dto -> {
                    assert dto.id().equals("1");
                    assert dto.message().equals("Great book!");
                });
    }

    @Test
    void testGetCommentNotFound() {
        given(commentService.findById("1")).willReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/book/comments/one/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testGetCommentsByBookId() {
        List<CommentDto> comments = List.of(
                new CommentDto("1", "Great book!"),
                new CommentDto("2", "Really enjoyed it!")
        );
        given(commentService.findByBookId("1")).willReturn(Flux.fromIterable(comments));

        webTestClient.get()
                .uri("/api/v1/book/comments/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(CommentDto.class)
                .hasSize(2)
                .value(list -> {
                    assert list.get(0).id().equals("1");
                    assert list.get(0).message().equals("Great book!");
                    assert list.get(1).id().equals("2");
                    assert list.get(1).message().equals("Really enjoyed it!");
                });
    }

    @Test
    void testAddComment() {
        CommentDto newComment = new CommentDto("1", "Nice book!");
        given(commentService.insert("Nice book!", "1")).willReturn(Mono.just(newComment));

        webTestClient.post()
                .uri("/api/v1/book/comments/1/add")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("message=Nice book!")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CommentDto.class)
                .value(dto -> {
                    assert dto.id().equals("1");
                    assert dto.message().equals("Nice book!");
                });
    }

    @Test
    void testUpdateComment() {
        CommentDto updatedComment = new CommentDto("1", "Updated comment");
        given(commentService.update("1", "Updated comment")).willReturn(Mono.just(updatedComment));

        webTestClient.put()
                .uri("/api/v1/book/comments/1/update")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("message=Updated comment")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CommentDto.class)
                .value(dto -> {
                    assert dto.id().equals("1");
                    assert dto.message().equals("Updated comment");
                });
    }

    @Test
    void testDeleteComment() {
        doNothing().when(commentService).deleteById("1");

        webTestClient.delete()
                .uri("/api/v1/book/comments/1/delete")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }
}