package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;

@Component
public class AuthorMapper {
    public AuthorDto toDTO(Author author) {
        return new AuthorDto(author.getId(), author.getFullName());
    }

    public Author toDocument(AuthorDto authorDTO) {
        return new Author(authorDTO.id(), authorDTO.fullName());
    }
}
