package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.mapper.GenreMapper;
import ru.otus.hw.models.Genre;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import({GenreServiceImpl.class, GenreMapper.class})
@DisplayName("Genre Service Tests")
class GenreServiceImplTest {
    @Autowired
    private GenreService genreService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        // Insert a test genre to ensure the collection is not empty
        Genre genre = new Genre();
        genre.setId("1");
        genre.setName("Test Genre");
        mongoTemplate.save(genre);
    }

    @DisplayName("Find all genres should return non-empty list")
    @Test
    void shouldFindAllGenres() {
        var genres = genreService.findAll();
        assertThat(genres)
                .isNotNull()
                .isNotEmpty()
                .allMatch(genre -> genre.name() != null && !genre.name().isEmpty());
    }
}