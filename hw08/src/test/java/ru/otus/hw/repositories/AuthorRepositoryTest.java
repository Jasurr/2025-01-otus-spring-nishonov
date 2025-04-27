package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Author;

import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class AuthorRepositoryTest {
    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void shouldFindAll() {
        var authors = authorRepository.findAll();
        assertFalse(authors.isEmpty());
    }
}