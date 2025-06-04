package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

@RestController
@RequiredArgsConstructor
public class CommentRestController {
    private final CommentService commentService;

    @GetMapping("/api/v1/book/comments/one/{id}")
    public Mono<CommentDto> getComment(@PathVariable("id") String id) {
        return commentService.findById(id);
    }

    @GetMapping("/api/v1/book/comments/{bookId}")
    public Flux<CommentDto> getComments(@PathVariable("bookId") String bookId) {
        return commentService.findByBookId(bookId);
    }

    @PostMapping("/api/v1/book/comments/{bookId}/add")
    public Mono<CommentDto> addComment(
            @PathVariable("bookId") String bookId,
            @RequestParam("message") String message) {
        return commentService.insert(message, bookId);
    }

    @PutMapping("/api/v1/book/comments/{commentId}/update")
    public Mono<CommentDto> updateComment(@PathVariable("commentId") String commentId,
                                              @RequestParam("message") String message) {
        return commentService.update(commentId, message);
    }

    @DeleteMapping("/api/v1/book/comments/{commentId}/delete")
    public Mono<Void> deleteComment(@PathVariable("commentId") String commentId) {
        return commentService.deleteById(commentId);
    }
}
