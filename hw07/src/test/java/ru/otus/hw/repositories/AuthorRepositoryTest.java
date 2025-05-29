package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
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