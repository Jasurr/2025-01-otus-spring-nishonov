package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuthorRepository.class)
@DisplayName("JpaAuthorRepository Test")
class JpaAuthorRepositoryTest {

    @Autowired
    private JpaAuthorRepository jpaAuthorRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("Should find all authors with single query")
    void shouldFindAllAuthors() {
        var authors = jpaAuthorRepository.findAll();

        assertThat(authors)
                .isNotNull()
                .isNotEmpty()
                .allMatch(a -> a.getFullName() != null && !a.getFullName().isEmpty());
    }
}