package ru.otus.hw.mapper;

import ru.otus.hw.dto.CommentDTO;
import ru.otus.hw.models.Comment;

public class CommentMapper {

    public static CommentDTO toDTO(Comment comment) {
        return new CommentDTO(comment.getId(),
                comment.getMessage(),
                comment.getBookId()
        );
    }

    public static Comment toDocument(CommentDTO commentDTO) {
        return new Comment(
                commentDTO.getId(),
                commentDTO.getMessage(),
                commentDTO.getBookId()
        );
    }
}
