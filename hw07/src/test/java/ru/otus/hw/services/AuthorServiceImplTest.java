package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(AuthorServiceImpl.class)
@DisplayName("Author Service Tests")
class AuthorServiceImplTest {
    @Autowired
    private AuthorService authorService;

    @DisplayName("Find all authors should return non-empty list")
    @Test
    void shouldFindAllAuthors() {
        var authors = authorService.findAll();
        assertThat(authors)
                .isNotNull()
                .isNotEmpty()
                .allMatch(author -> author.getFullName() != null && !author.getFullName().isEmpty());
    }
}