package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.config.TestMongockConfig;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.models.Book;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import({CommentServiceImpl.class, TestMongockConfig.class, CommentMapper.class})
@DisplayName("Tests for CommentService")
class CommentServiceTest {

    private static final String COMMENT_MESSAGE = "Test comment";
    private static final String UPDATED_COMMENT_MESSAGE = "Updated comment";

    @Autowired
    private CommentService commentService;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = getAnyBook();
        assertThat(testBook).isNotNull();
    }

    @Test
    @DisplayName("Should find comment by ID")
    void shouldFindCommentById() {
        Mono<CommentDto> savedCommentMono = commentService.insert(COMMENT_MESSAGE, testBook.getId());

        StepVerifier.create(savedCommentMono)
                .assertNext(savedComment -> commentService.findById(savedComment.id())
                        .as(StepVerifier::create)
                        .assertNext(foundComment -> assertThat(foundComment)
                                .hasFieldOrPropertyWithValue("message", COMMENT_MESSAGE))
                        .verifyComplete())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find comments by book ID")
    void shouldFindCommentsByBookId() {
        Mono<CommentDto> savedCommentMono = commentService.insert(COMMENT_MESSAGE, testBook.getId());

        StepVerifier.create(savedCommentMono)
                .assertNext(savedComment -> commentService.findByBookId(testBook.getId()).collectList()
                        .as(StepVerifier::create)
                        .assertNext(comments -> assertThat(comments)
                                .isNotNull()
                                .isNotEmpty()
                                .allMatch(comment -> comment.message() != null && !comment.message().isEmpty())
                                .anyMatch(comment -> comment.id().equals(savedComment.id()) && comment.message().equals(COMMENT_MESSAGE)))
                        .verifyComplete())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should insert comment")
    void shouldInsertComment() {
        StepVerifier.create(commentService.insert(COMMENT_MESSAGE, testBook.getId()))
                .assertNext(savedComment -> assertThat(savedComment)
                        .isNotNull()
                        .hasFieldOrPropertyWithValue("message", COMMENT_MESSAGE))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update comment")
    void shouldUpdateComment() {
        Mono<CommentDto> savedCommentMono = commentService.insert(COMMENT_MESSAGE, testBook.getId());

        StepVerifier.create(savedCommentMono)
                .assertNext(savedComment -> commentService.update(savedComment.id(), UPDATED_COMMENT_MESSAGE)
                        .as(StepVerifier::create)
                        .assertNext(updatedComment -> assertThat(updatedComment)
                                .isNotNull()
                                .hasFieldOrPropertyWithValue("message", UPDATED_COMMENT_MESSAGE))
                        .verifyComplete())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should delete comment")
    void shouldDeleteComment() {
        Mono<CommentDto> savedCommentMono = commentService.insert(COMMENT_MESSAGE, testBook.getId());

        StepVerifier.create(savedCommentMono)
                .assertNext(savedComment -> {
                    commentService.deleteById(savedComment.id())
                            .as(StepVerifier::create)
                            .verifyComplete();
                    commentService.findById(savedComment.id())
                            .as(StepVerifier::create)
                            .expectNextCount(0)
                            .verifyComplete();
                })
                .verifyComplete();
    }

    private Book getAnyBook() {
        return reactiveMongoTemplate.findAll(Book.class).filter(b -> b.getId() != null)
                .next()
                .block();
    }
}