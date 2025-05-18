package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(AuthorServiceImpl.class)
@DisplayName("Author Service Tests")
public class AuthorServiceImplTest {
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
