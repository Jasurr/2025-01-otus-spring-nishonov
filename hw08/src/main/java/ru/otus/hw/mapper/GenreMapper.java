package ru.otus.hw.mapper;

import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;

public class GenreMapper {
    public static GenreDto toDTO(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }

    public static Genre toDocument(GenreDto genre) {
        return new Genre(genre.id(), genre.name());
    }
}
