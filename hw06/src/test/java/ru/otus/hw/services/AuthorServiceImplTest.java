package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.repositories.JpaAuthorRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({AuthorServiceImpl.class, JpaAuthorRepository.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("AuthorService Tests")
class AuthorServiceImplTest {

    @Autowired
    private AuthorService authorService;

    @Test
    @DisplayName("Should find all authors without LazyInitializationException")
    void shouldFindAllAuthors() {
        var authors = authorService.findAll();
        assertThat(authors)
                .isNotNull()
                .isNotEmpty()
                .allMatch(author -> author.getFullName() != null && !author.getFullName().isEmpty());
    }
}