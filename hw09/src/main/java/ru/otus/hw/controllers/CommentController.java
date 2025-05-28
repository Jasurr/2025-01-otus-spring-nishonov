package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.List;

@Controller
@RequestMapping("/book/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/{id}")
    @ResponseBody
    public List<CommentDto> getComments(@PathVariable Long id) {
        return commentService.findByBookId(id);
    }

    @PostMapping("/{id}")
    @ResponseBody
    public void addComment(@PathVariable Long id, @RequestBody CommentDto comment) {
        commentService.insert(comment.message(), id);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateComment(@PathVariable("id") Long commentId, @RequestBody CommentDto comment) {
        commentService.update(commentId, comment.message());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteById(id);
    }
}
