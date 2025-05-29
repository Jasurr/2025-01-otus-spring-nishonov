package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Comment;

@Component
public class CommentMapper {
    public CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getMessage(),
                new BookDto(
                        comment.getBook().getId(),
                        comment.getBook().getTitle(),
                        new AuthorDto(
                                comment.getBook().getAuthor().getId(),
                                comment.getBook().getAuthor().getFullName()),
                        comment.getBook().getGenres().stream()
                                .map(genre -> new GenreDto(genre.getId(), genre.getName()))
                                .toList()
                )
        );
    }
}
