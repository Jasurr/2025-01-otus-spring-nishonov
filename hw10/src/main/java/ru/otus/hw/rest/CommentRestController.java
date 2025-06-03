package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentRestController {
    private final CommentService commentService;

    @GetMapping("/api/v1/book/comments/one/{id}")
    public ResponseEntity<CommentDto> getComment(@PathVariable("id") Long id) {
        return commentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/v1/book/comments/{bookId}")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable("bookId") Long bookId) {
        return ResponseEntity.ok(commentService.findByBookId(bookId));
    }

    @PostMapping("/api/v1/book/comments/{bookId}/add")
    public ResponseEntity<Void> addComment(
            @PathVariable("bookId") Long bookId,
            @RequestParam("message") String message) {
        commentService.insert(message, bookId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/v1/book/comments/{commentId}/update")
    public ResponseEntity<Void> updateComment(@PathVariable("commentId") Long commentId,
                                              @RequestParam("message") String message) {
        commentService.update(commentId, message);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/v1/book/comments/{commentId}/delete")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.deleteById(commentId);
        return ResponseEntity.ok().build();
    }
}
