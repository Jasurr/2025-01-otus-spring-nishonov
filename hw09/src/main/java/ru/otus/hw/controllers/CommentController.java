package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.dto.CommentDTO;
import ru.otus.hw.services.CommentService;

import java.util.List;

@Controller
@RequestMapping("/book/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/{id}")
    @ResponseBody
    public List<CommentDTO> getComments(@PathVariable Long id) {
        return commentService.findByBookId(id);
    }

    @PostMapping("/{id}")
    @ResponseBody
    public void addComment(@PathVariable Long id, @RequestBody CommentDTO comment) {
        commentService.insert(comment.getMessage(), id);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateComment(@PathVariable("id") Long commentId, @RequestBody CommentDTO comment) {
        commentService.update(commentId, comment.getMessage(), comment.getBookId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteById(id);
    }
}
