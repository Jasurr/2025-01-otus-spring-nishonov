package ru.otus.hw.mapper;

import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;

public class GenreMapper {

    public static Genre toModel(GenreDto dto) {
        return new Genre(
                dto.getId(),
                dto.getName()
        );
    }

    public static GenreDto toDto(Genre genre) {
        return new GenreDto(
                genre.getId(),
                genre.getName()
        );
    }
}
