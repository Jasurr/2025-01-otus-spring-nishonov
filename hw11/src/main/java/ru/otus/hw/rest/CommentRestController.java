package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CommentRequest;
import ru.otus.hw.services.CommentService;

@RestController
@RequiredArgsConstructor
public class CommentRestController {
    private final CommentService commentService;

    @GetMapping("/api/v1/book/comments/{id}")
    public Mono<CommentDto> getComment(@PathVariable("id") String id) {
        return commentService.findById(id);
    }

    @GetMapping("/api/v1/book/comments")
    public Flux<CommentDto> getComments(@RequestParam("bookId") String bookId) {
        return commentService.findByBookId(bookId);
    }

    @PostMapping("/api/v1/book/comments")
    public Mono<CommentDto> addComment(@RequestBody CommentRequest commentRequest) {
        return commentService.insert(commentRequest.message(), commentRequest.bookId());
    }

    @PutMapping("/api/v1/book/comments")
    public Mono<CommentDto> updateComment(@RequestBody CommentDto commentDto) {
        return commentService.update(commentDto.id(), commentDto.message());
    }

    @DeleteMapping("/api/v1/book/comments/{commentId}")
    public Mono<Void> deleteComment(@PathVariable("commentId") String commentId) {
        return commentService.deleteById(commentId);
    }
}
