package ru.otus.hw.repositories;

import org.hibernate.SessionFactory;
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
        SessionFactory sessionFactory = em.getEntityManager().getEntityManagerFactory().unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);
        sessionFactory.getStatistics().clear();

        var authors = jpaAuthorRepository.findAll();
        assertThat(authors)
                .isNotNull()
                .isNotEmpty()
                .allMatch(a -> a.getFullName() != null && !a.getFullName().isEmpty());

        assertThat(sessionFactory.getStatistics().getQueryExecutionCount())
                .isEqualTo(1); // Ensure only one query is executed
    }
}