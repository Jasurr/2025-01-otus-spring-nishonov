package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CommentRequest;
import ru.otus.hw.services.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentRestController {
    private final CommentService commentService;

    @GetMapping("/api/v1/book/comments/{id}")
    public ResponseEntity<CommentDto> getComment(@PathVariable("id") Long id) {
        return commentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/v1/book/comments")
    public ResponseEntity<List<CommentDto>> getComments(@RequestParam("bookId") Long bookId) {
        return ResponseEntity.ok(commentService.findByBookId(bookId));
    }

    @PostMapping("/api/v1/book/comments")
    public ResponseEntity<Void> addComment(@RequestBody CommentRequest commentRequest) {
        commentService.insert(commentRequest.message(), commentRequest.bookId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/v1/book/comments")
    public ResponseEntity<Void> updateComment(@RequestBody CommentDto commentDto) {
        commentService.update(commentDto.id(), commentDto.message());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/v1/book/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.deleteById(commentId);
        return ResponseEntity.ok().build();
    }
}
