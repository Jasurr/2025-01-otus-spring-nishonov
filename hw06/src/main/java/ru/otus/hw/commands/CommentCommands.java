package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {
    private final CommentService commentService;

    private final CommentConverter commentConverter;

    @ShellMethod(value = "Find all comments", key = "ac")
    public String findCommentById(long id) {
        return commentService.findById(id)
                .map(comment -> commentConverter.commentToString(
                        comment.getId(),
                        comment.getMessage(),
                        comment.getBook().getId()))
                .orElse("Comment with id %d not found".formatted(id));
    }

    @ShellMethod(value = "Find all comments by book id", key = "acb")
    public String findCommentByBookId(long bookId) {
        return commentService.findByBookId(bookId).stream()
                .map(comment -> commentConverter.commentToString(
                        comment.getId(),
                        comment.getMessage(),
                        comment.getBook().getId()))
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Insert comment", key = "cins")
    public String insertComment(String message, long bookId) {
        var savedComment = commentService.insert(message, bookId);
        return commentConverter.commentToString(
                savedComment.getId(),
                savedComment.getMessage(),
                savedComment.getBook().getId());
    }

    @ShellMethod(value = "Update comment", key = "cupd")
    public String updateComment(long id, String message) {
        var updatedComment = commentService.update(id, message);
        return commentConverter.commentToString(
                updatedComment.getId(),
                updatedComment.getMessage(),
                updatedComment.getBook().getId());
    }

    @ShellMethod(value = "Delete comment by id", key = "cdel")
    public void deleteComment(long id) {
        commentService.deleteById(id);
    }
}
