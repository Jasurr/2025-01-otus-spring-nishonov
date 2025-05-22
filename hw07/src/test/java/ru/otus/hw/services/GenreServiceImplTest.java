package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(GenreServiceImpl.class)
@DisplayName("Genre Service Tests")
class GenreServiceImplTest {
    @Autowired
    private GenreService genreService;

    @DisplayName("Find all genres should return non-empty list")
    @Test
    void shouldFindAllGenres() {
        var genres = genreService.findAll();
        assertThat(genres)
                .isNotNull()
                .isNotEmpty()
                .allMatch(genre -> genre.getName() != null && !genre.getName().isEmpty());
    }
}