package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.repositories.JpaGenreRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({GenreServiceImpl.class, JpaGenreRepository.class})
@DisplayName("Genre Service Tests")
class GenreServiceImplTest {
    @Autowired
    private GenreService genreService;

    @DisplayName("Should find all genres without LazyInitializationException")
    @Test
    void shouldFindAllGenres() {
        var genres = genreService.findAll();
        assertThat(genres)
                .as("Genres list should not be null")
                .isNotNull()
                .as("Genres list should not be empty")
                .isNotEmpty()
                .as("Each genre name should be null or have length > 0")
                .allSatisfy(genre ->
                        assertThat(genre.getName() == null || !genre.getName().isBlank())
                                .as("Genre name is either null or non-blank")
                                .isTrue()
                );

    }
}