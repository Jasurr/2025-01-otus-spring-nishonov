package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import reactor.test.StepVerifier;
import ru.otus.hw.mapper.GenreMapper;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import({GenreServiceImpl.class, GenreMapper.class})
@DisplayName("Genre Service Tests for MongoDB")
class GenreServiceImplTest {

    @Autowired
    private GenreService genreService;

    @DisplayName("Find all genres should return non-empty list")
    @Test
    void shouldFindAllGenres() {
        StepVerifier.create(genreService.findAll().collectList())
                .assertNext(genres -> assertThat(genres)
                        .as("Genres should not be null")
                        .isNotNull()
                        .as("Genres should not be empty")
                        .isNotEmpty()
                        .allMatch(genre -> genre.name() != null && !genre.name().isEmpty())
                )
                .verifyComplete();
    }
}