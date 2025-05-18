package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@SpringBootTest
@ActiveProfiles("test")
class GenreRepositoryTest {
    @Autowired
    private GenreRepository genreRepository;

    @Test
    void shouldFindAll() {
        var genres = genreRepository.findAll();
        assertThat(genres)
                .isNotNull()
                .isNotEmpty()
                .allMatch(genre -> genre.getName() != null && !genre.getName().isEmpty());
    }

}
