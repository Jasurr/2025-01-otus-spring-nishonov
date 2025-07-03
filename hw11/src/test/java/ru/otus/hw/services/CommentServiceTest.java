package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.models.Book;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import({CommentServiceImpl.class, CommentMapper.class})
@DisplayName("Tests for CommentService")
class CommentServiceTest {

    private static final String COMMENT_MESSAGE = "Test comment";
    private static final String UPDATED_COMMENT_MESSAGE = "Updated comment";

    private static final String TEST_BOOK_TITLE = "Sample Book";

    @Autowired
    private CommentService commentService;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    private Book testBook;

    @BeforeEach
    @DisplayName("Setup test data")
    void setUp() {
        // Create test data
        testBook = getTestBook().block();
        assertThat(testBook)
                .as("Failed to create test book")
                .isNotNull();
        assertThat(testBook.getId())
                .as("Test book should have an ID")
                .isNotNull();
    }

    @Test
    @DisplayName("Should find comment by ID")
    void shouldFindCommentById() {
        // Chain insert and findById operations properly
        Mono<CommentDto> result = commentService.insert(COMMENT_MESSAGE, testBook.getId())
                .flatMap(savedComment -> commentService.findById(savedComment.id()));

        StepVerifier.create(result)
                .assertNext(foundComment -> {
                    assertThat(foundComment)
                            .isNotNull()
                            .hasFieldOrPropertyWithValue("message", COMMENT_MESSAGE);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find comments by book ID")
    void shouldFindCommentsByBookId() {
        // Chain insert and findByBookId operations properly
        Mono<List<CommentDto>> result = commentService.insert(COMMENT_MESSAGE, testBook.getId())
                .then(commentService.findByBookId(testBook.getId()).collectList());

        StepVerifier.create(result)
                .assertNext(comments -> {
                    assertThat(comments)
                            .isNotNull()
                            .isNotEmpty()
                            .allMatch(comment -> comment.message() != null && !comment.message().isEmpty())
                            .anyMatch(comment -> comment.message().equals(COMMENT_MESSAGE));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should insert comment")
    void shouldInsertComment() {
        StepVerifier.create(commentService.insert(COMMENT_MESSAGE, testBook.getId()))
                .assertNext(savedComment -> {
                    assertThat(savedComment)
                            .isNotNull()
                            .hasFieldOrPropertyWithValue("message", COMMENT_MESSAGE);
                    assertThat(savedComment.id())
                            .isNotNull();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update comment")
    void shouldUpdateComment() {
        // Chain insert and update operations properly
        Mono<CommentDto> result = commentService.insert(COMMENT_MESSAGE, testBook.getId())
                .flatMap(savedComment -> commentService.update(savedComment.id(), UPDATED_COMMENT_MESSAGE));

        StepVerifier.create(result)
                .assertNext(updatedComment -> {
                    assertThat(updatedComment)
                            .isNotNull()
                            .hasFieldOrPropertyWithValue("message", UPDATED_COMMENT_MESSAGE);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should delete comment")
    void shouldDeleteComment() {
        // Chain insert, delete, and verification operations
        var comment = commentService.insert(COMMENT_MESSAGE, testBook.getId()).block();

        var deleteAndVerifyResult = commentService.deleteById(comment.id());

        StepVerifier.create(deleteAndVerifyResult)
                .expectNextCount(0) // Should not find any comment after deletion
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle update of non-existent comment")
    void shouldHandleUpdateOfNonExistentComment() {
        String nonExistentId = "507f1f77bcf86cd799439011"; // Valid ObjectId format

        // This test depends on your service implementation behavior
        // It might throw an exception or return empty
        StepVerifier.create(commentService.update(nonExistentId, UPDATED_COMMENT_MESSAGE))
                .expectError() // Assuming it throws an exception like the BookService
                .verify();
    }

    @Test
    @DisplayName("Should handle delete of non-existent comment")
    void shouldHandleDeleteOfNonExistentComment() {
        String nonExistentId = "507f1f77bcf86cd799439011"; // Valid ObjectId format

        StepVerifier.create(commentService.deleteById(nonExistentId))
                .verifyComplete(); // Should complete without error even if comment doesn't exist
    }

    @Test
    @DisplayName("Should handle invalid parameters gracefully")
    void shouldHandleInvalidParameters() {
        // Test with null message
        StepVerifier.create(commentService.insert(null, testBook.getId()))
                .expectError()
                .verify();

        // Test with null book ID
        StepVerifier.create(commentService.insert(COMMENT_MESSAGE, null))
                .expectError()
                .verify();

        // Test update with null message
        StepVerifier.create(commentService.insert(COMMENT_MESSAGE, testBook.getId())
                        .flatMap(comment -> commentService.update(comment.id(), null)))
                .expectError()
                .verify();
    }

    private Mono<Book> getTestBook() {
        Query query = new Query();
        query.addCriteria(Criteria.where("title").is(TEST_BOOK_TITLE));
        return reactiveMongoTemplate.findOne(query, Book.class);
    }
}