package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.config.TestMongockConfig;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import(TestMongockConfig.class)
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void shouldFindAll() {
        var authors = authorRepository.findAll();
        assertThat(authors)
                .isNotNull()
                .isNotEmpty()
                .allMatch(a -> a.getFullName() != null && !a.getFullName().isEmpty());
    }
}