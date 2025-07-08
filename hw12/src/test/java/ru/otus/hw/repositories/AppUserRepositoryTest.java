package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository repository;

    @Autowired
    private TestEntityManager em;


    @Test
    void shouldFindAllUsers() {
        // Assuming some test data is preloaded in the test database
        var users = repository.findAll();
        assertThat(users)
                .isNotNull()
                .isNotEmpty()
                .allMatch(u -> u.getUsername() != null && !u.getUsername().isEmpty());
    }
}