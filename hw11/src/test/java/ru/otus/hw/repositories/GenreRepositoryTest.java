package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.test.StepVerifier;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataMongoTest
class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void shouldFindAll() {
        StepVerifier.create(genreRepository.findAll().collectList())
                .assertNext(genres -> assertThat(genres)
                        .isNotNull()
                        .isNotEmpty()
                        .allMatch(genre -> genre.getName() != null && !genre.getName().isEmpty()))
                .verifyComplete();
    }
}