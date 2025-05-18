package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.GenreDTO;

@Component
public class GenreConverter {
    public String genreToString(GenreDTO genre) {
        return "Id: %s, Name: %s".formatted(genre.getId(), genre.getName());
    }
}
