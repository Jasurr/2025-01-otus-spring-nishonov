package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.AppUser;
import ru.otus.hw.models.Role;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository repository;

    @Autowired
    private TestEntityManager em;


    @Test
    void shouldFindAllUsers() {
        var savedUser = new AppUser();
        savedUser.setPassword("password");
        savedUser.setUsername("username");
        savedUser.setRoles(Set.of(new Role(1L, "ADMIN"), new Role(2L, "USER")));
        em.persist(savedUser);
        em.flush();
        // Assuming some test data is preloaded in the test database
        var users = repository.findAll();
        assertThat(users)
                .isNotNull()
                .isNotEmpty()
                .allMatch(u -> u.getUsername() != null && !u.getUsername().isEmpty());
    }
}