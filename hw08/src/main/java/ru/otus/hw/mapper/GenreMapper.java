package ru.otus.hw.mapper;

import ru.otus.hw.dto.GenreDTO;
import ru.otus.hw.models.Genre;

public class GenreMapper {
    public static GenreDTO toDTO(Genre genre) {
        return new GenreDTO(genre.getId(), genre.getName());
    }

    public static Genre toDocument(GenreDTO genre) {
        return new Genre(genre.getId(), genre.getName());
    }
}
