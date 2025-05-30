package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

@Controller
@RequestMapping("/book/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/{bookId}")
    public String getComments(@PathVariable("bookId") Long bookId, Model model) {
        model.addAttribute("comments", commentService.findByBookId(bookId));
        model.addAttribute("bookId", bookId);
        return "comment/list";
    }

    @PostMapping("/{bookId}/add")
    public String addComment(@PathVariable("bookId") Long bookId, @RequestParam("message") String message) {
        commentService.insert(message, bookId);
        return "redirect:/book/comment/" + bookId;
    }

    @GetMapping("/{commentId}/edit")
    public String showEditCommentForm(@PathVariable("commentId") Long commentId,
                                      @RequestParam("bookId") Long bookId, Model model) {
        try {
            CommentDto comment = commentService.findById(commentId).get();
            model.addAttribute("comment", comment);
            model.addAttribute("bookId", bookId);
            return "comment/edit";
        } catch (RuntimeException e) {
            return "redirect:/book/comment/" + bookId + "?error=Comment+not+found";
        }
    }

    @PostMapping("/{commentId}/update")
    public String updateComment(@PathVariable("commentId") Long commentId,
                                @RequestParam("message") String message,
                                @RequestParam("bookId") Long bookId) {
        try {
            commentService.update(commentId, message); // Use the updated method signature
            return "redirect:/book/comment/" + bookId;
        } catch (RuntimeException e) {
            return "redirect:/book/comment/" + bookId + "?error=Failed+to+update+comment";
        }
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable("commentId") Long commentId,
                                @RequestParam("bookId") Long bookId) {
        try {
            commentService.deleteById(commentId);
            return "redirect:/book/comment/" + bookId;
        } catch (RuntimeException e) {
            return "redirect:/book/comment/" + bookId + "?error=Failed+to+delete+comment";
        }
    }
}