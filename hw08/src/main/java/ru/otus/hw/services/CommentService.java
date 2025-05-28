package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.SimpleCommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    Optional<SimpleCommentDto> findById(String id);

    List<SimpleCommentDto> findByBookId(String bookId);

    CommentDto insert(String message, String bookId);

    CommentDto update(String id, String message);

    void deleteById(String id);
}
