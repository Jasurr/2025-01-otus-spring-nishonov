package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.config.TestMongockConfig;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mapper.GenreMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import({TestMongockConfig.class, GenreServiceImpl.class, GenreMapper.class})
@DisplayName("Genre Service Tests for MongoDB")
class GenreServiceImplTest {

    @Autowired
    private GenreService genreService;

    @DisplayName("Find all genres should return non-empty list")
    @Test
    void shouldFindAllGenres() {
        List<GenreDto> genres = genreService.findAll();
        assertThat(genres)
                .isNotNull()
                .isNotEmpty()
                .allMatch(genre -> genre.name() != null && !genre.name().isEmpty());
    }
}