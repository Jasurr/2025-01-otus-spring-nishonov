package ru.otus.hw.mapper;

import ru.otus.hw.dto.AuthorDTO;
import ru.otus.hw.models.Author;

public class AuthorMapper {
    public static AuthorDTO toDTO(Author author) {
        return new AuthorDTO(author.getId(), author.getFullName());
    }

    public static Author toDocument(AuthorDTO authorDTO) {
        return new Author(authorDTO.getId(), authorDTO.getFullName());
    }
}
