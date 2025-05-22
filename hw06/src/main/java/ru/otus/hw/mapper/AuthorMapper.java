package ru.otus.hw.mapper;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;

public class AuthorMapper {

    public static AuthorDto toDto(Author author) {
        return new AuthorDto(
                author.getId(),
                author.getFullName()
        );
    }
}
