package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Genre Service Tests")
class GenreServiceImplTest {
    @Autowired
    private GenreService genreService;

    @DisplayName("Find all genres should return non-empty list")
    @Test
    void shouldFindAllGenres() {
        var genres = genreService.findAll();
        assertFalse(genres.isEmpty());
    }
}