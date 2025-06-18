package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import reactor.test.StepVerifier;
import ru.otus.hw.config.TestMongockConfig;
import ru.otus.hw.mapper.AuthorMapper;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import({TestMongockConfig.class, AuthorServiceImpl.class, AuthorMapper.class})
@DisplayName("Author Service Tests for MongoDB")
class AuthorServiceImplTest {

    @Autowired
    private AuthorService authorService;

    @DisplayName("Find all authors should return non-empty flux")
    @Test
    void shouldFindAllAuthors() {
        StepVerifier.create(authorService.findAll().collectList())
                .assertNext(authors -> assertThat(authors)
                        .as("Authors should not be null")
                        .isNotNull()
                        .as("Authors should not be empty")
                        .isNotEmpty()
                        .allMatch(author -> author.fullName() != null && !author.fullName().isEmpty()))
                .verifyComplete();
    }
}