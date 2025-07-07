package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.migrate.InitialTestDataMigration;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@DisplayName("Comment Repository Tests for MongoDB (Reactive)")
@Import(InitialTestDataMigration.class)
class CommentRepositoryTest {

    private static final String TEST_COMMENT_MESSAGE = "Test comment";
    private static final String UPDATED_COMMENT_MESSAGE = "Updated comment";
    private static final String TEST_BOOK_TITLE = "Sample Book";

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    private Book book;

    @BeforeEach
    void setUp() {
        // Create a test book
        Book testBook = new Book();
        testBook.setTitle(TEST_BOOK_TITLE);
        // Clear comments and insert the test book
        Mono<Void> clearComments = reactiveMongoTemplate.remove(new Query(), Comment.class).then();
        Mono<Book> saveBook = reactiveMongoTemplate.save(testBook);

        // Chain operations and verify
        StepVerifier.create(
                        clearComments
                                .then(saveBook)
                                .doOnNext(savedBook -> {
                                    System.out.println("Saved book: " + savedBook);
                                    this.book = savedBook; // Store the saved book
                                })
                                .doOnError(error -> System.err.println("Error setting up test data: " + error.getMessage()))
                )
                .assertNext(savedBook -> {
                    assertThat(savedBook)
                            .as("Test book '" + TEST_BOOK_TITLE + "' should be saved")
                            .isNotNull()
                            .hasFieldOrPropertyWithValue("title", TEST_BOOK_TITLE);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should save comment")
    void shouldSaveComment() {
        Comment comment = createTestComment();

        StepVerifier.create(
                        commentRepository.save(comment)
                                .flatMap(savedComment ->
                                        reactiveMongoTemplate.findOne(
                                                Query.query(Criteria.where("_id").is(savedComment.getId())),
                                                Comment.class
                                        )
                                )
                )
                .assertNext(commentFromDB -> {
                    assertThat(commentFromDB).isNotNull();
                    assertThat(commentFromDB.getId()).isNotBlank();
                    assertThat(commentFromDB.getMessage()).isEqualTo(TEST_COMMENT_MESSAGE);
                    assertThat(commentFromDB.getBook().getId()).isEqualTo(book.getId());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find comments by book ID")
    void shouldFindByBookId() {
        // Given
        Comment comment = createTestComment();
        StepVerifier.create(reactiveMongoTemplate.save(comment))
                .expectNextCount(1)
                .verifyComplete();

        // When
        Flux<Comment> commentsFlux = commentRepository.findByBookId(book.getId());

        // Then
        StepVerifier.create(commentsFlux.collectList())
                .assertNext(comments -> assertThat(comments)
                        .isNotEmpty()
                        .hasSize(1)
                        .first()
                        .satisfies(c -> {
                            assertThat(c.getMessage()).isEqualTo(TEST_COMMENT_MESSAGE);
                            assertThat(c.getBook().getId()).isEqualTo(book.getId());
                        }))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update saved comment")
    void shouldUpdateComment() {
        Comment comment = createTestComment();

        StepVerifier.create(
                        reactiveMongoTemplate.save(comment)
                                .flatMap(savedComment -> {
                                    savedComment.setMessage(UPDATED_COMMENT_MESSAGE);
                                    return commentRepository.save(savedComment);
                                })
                                .flatMap(updatedComment ->
                                        reactiveMongoTemplate.findOne(
                                                Query.query(Criteria.where("_id").is(updatedComment.getId())),
                                                Comment.class
                                        )
                                )
                )
                .assertNext(commentFromDB -> {
                    assertThat(commentFromDB).isNotNull();
                    assertThat(commentFromDB.getMessage()).isEqualTo(UPDATED_COMMENT_MESSAGE);
                    assertThat(commentFromDB.getBook().getId()).isEqualTo(book.getId());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle empty results when no comments exist for book")
    void shouldReturnEmptyListWhenNoCommentsForBook() {
        // When
        Flux<Comment> commentsFlux = commentRepository.findByBookId(book.getId());

        // Then
        StepVerifier.create(commentsFlux)
                .expectNextCount(0)
                .verifyComplete();
    }

    private Comment createTestComment() {
        Comment comment = new Comment();
        comment.setMessage(TEST_COMMENT_MESSAGE);
        comment.setBook(book);
        return comment;
    }
}