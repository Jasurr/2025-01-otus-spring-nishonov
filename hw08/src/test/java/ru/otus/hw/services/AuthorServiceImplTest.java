package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Author;
import ru.otus.hw.mapper.AuthorMapper;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import({AuthorServiceImpl.class, AuthorMapper.class})
@DisplayName("Author Service Tests")
class AuthorServiceImplTest {
    @Autowired
    private AuthorService authorService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        // Insert a test author to ensure the collection is not empty
        Author author = new Author();
        author.setId("1");
        author.setFullName("Test Author");
        mongoTemplate.save(author);
    }

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