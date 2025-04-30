package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaGenreRepository.class)
@DisplayName("JpaGenreRepository Test")
class JpaGenreRepositoryTest {

    @Autowired
    private JpaGenreRepository jpaGenreRepository;

    @Test
    @DisplayName("Should find all genres")
    void shouldFindAllGenres() {
        var genres = jpaGenreRepository.findAll();
        assertThat(genres)
                .isNotNull()
                .isNotEmpty()
                .allMatch(genre -> genre.getName() != null && !genre.getName().isEmpty());
    }
}