package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void shouldFindAll() {
        StepVerifier.create(authorRepository.findAll().collectList())
                .assertNext(authors -> assertThat(authors)
                        .as("Authors should not be null")
                        .isNotNull()
                        .as("Authors should not be empty")
                        .isNotEmpty()
                        .allMatch(author -> author.getFullName() != null && !author.getFullName().isEmpty())
                ).verifyComplete();
    }

    @Test
    void shouldCheckExistsById() {
        // Assuming an author with a known ID exists from TestMongockConfig
        String existingAuthorId = "684983af9934676eff037a6e"; // Replace with a valid ID from your test data
        authorRepository.existsById(existingAuthorId)
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }
}