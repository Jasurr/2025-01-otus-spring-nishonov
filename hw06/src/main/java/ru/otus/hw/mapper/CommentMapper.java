package ru.otus.hw.mapper;

import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;

public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getMessage(),
                BookMapper.toDto(comment.getBook())
        );
    }
}
