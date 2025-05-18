package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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