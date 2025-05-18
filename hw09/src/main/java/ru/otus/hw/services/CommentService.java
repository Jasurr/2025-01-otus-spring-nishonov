package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDTO;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    Optional<CommentDTO> findById(long id);

    List<CommentDTO> findByBookId(long bookId);

    CommentDTO insert(String message, long bookId);

    CommentDTO update(long id, String message, long bookId);

    void deleteById(long id);
}
