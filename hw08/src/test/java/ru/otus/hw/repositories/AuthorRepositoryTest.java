package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.models.Author;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void shouldFindAll() {
        List<Author> authors = authorRepository.findAll();
        assertThat(authors)
                .isNotNull()
                .isNotEmpty()
                .allMatch(a -> a.getFullName() != null && !a.getFullName().isEmpty());
    }
}
