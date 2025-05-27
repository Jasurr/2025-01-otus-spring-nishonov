package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    Optional<Comment> findById(String id);

    List<Comment> findByBookId(String bookId);

    Comment insert(String message, String bookId);

    CommentDto update(String id, String message, String bookId);

    void deleteById(String id);
}
