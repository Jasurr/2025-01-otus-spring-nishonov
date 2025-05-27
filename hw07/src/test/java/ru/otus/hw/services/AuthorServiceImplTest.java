package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.mapper.AuthorMapper;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({AuthorServiceImpl.class, AuthorMapper.class})
@DisplayName("Author Service Tests")
class AuthorServiceImplTest {
    @Autowired
    private AuthorService authorService;

    @DisplayName("Find all authors should return non-empty list")
    @Test
    void shouldFindAllAuthors() {
        var authors = authorService.findAll();
        assertThat(authors)
                .as("Authors should not be null")
                .isNotNull()
                .as("Authors should not be empty")
                .isNotEmpty()
                .allMatch(author -> author.fullName() != null && !author.fullName().isEmpty());
    }
}