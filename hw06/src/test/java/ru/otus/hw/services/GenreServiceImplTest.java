package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.repositories.JpaAuthorRepository;
import ru.otus.hw.repositories.JpaGenreRepository;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.StringUtils.hasLength;

@DataJpaTest
@Import({GenreServiceImpl.class, JpaGenreRepository.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Genre Service Tests")
class GenreServiceImplTest {
    @Autowired
    private GenreService genreService;

    @DisplayName("Should find all genres without LazyInitializationException")
    @Test
    void shouldFindAllGenres() {
        var genres = genreService.findAll();
        assertThat(genres)
                .isNotNull()
                .isNotEmpty()
                .allMatch(genre -> isNull(genre.getName()) || hasLength(genre.getName()));
    }
}