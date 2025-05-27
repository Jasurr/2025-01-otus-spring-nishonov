package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.SimpleCommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    Optional<SimpleCommentDto> findById(long id);

    List<SimpleCommentDto> findByBookId(long bookId);

    CommentDto insert(String message, long bookId);

    CommentDto update(long id, String message);

    void deleteById(long id);
}
