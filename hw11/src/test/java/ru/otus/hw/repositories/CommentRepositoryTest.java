package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw.config.TestMongockConfig;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import(TestMongockConfig.class)
@DisplayName("Comment Repository Tests for MongoDB")
class CommentRepositoryTest {

    private static final String TEST_COMMENT_MESSAGE = "Test comment";
    private static final String UPDATED_COMMENT_MESSAGE = "Updated comment";
    private static final String TEST_BOOK_TITLE = "Sample Book";

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Book book;

    @BeforeEach
    void setUp() {
        // Clear any existing comments to ensure test isolation
        mongoTemplate.remove(new Query(), Comment.class);

        // Fetch a Book from the migrated data with null check
        book = mongoTemplate.findOne(
                Query.query(Criteria.where("title").is(TEST_BOOK_TITLE)), Book.class);

        // Skip tests if the required book is not found
        assertThat(book)
                .as("Test book '" + TEST_BOOK_TITLE + "' not found in test data")
                .isNotNull();
    }

    @Test
    @DisplayName("Should save comment")
    void shouldSaveComment() {
        Comment comment = createTestComment();
        Comment savedComment = commentRepository.save(comment);

        // Verify the saved comment has an ID
        assertThat(savedComment.getId()).isNotNull().isNotBlank();

        // Fetch from database to verify persistence
        Comment commentFromDB = mongoTemplate.findOne(
                Query.query(Criteria.where("_id").is(savedComment.getId())), Comment.class);

        assertThat(commentFromDB)
                .as("Comment should be found in database")
                .isNotNull();
        assertThat(commentFromDB.getId()).isEqualTo(savedComment.getId());
        assertThat(commentFromDB.getMessage()).isEqualTo(TEST_COMMENT_MESSAGE);
        assertThat(commentFromDB.getBook().getId()).isEqualTo(book.getId());
    }

    @Test
    @DisplayName("Should find comments by book ID")
    void shouldFindByBookId() {
        Comment comment = createTestComment();
        mongoTemplate.save(comment);

        List<Comment> comments = commentRepository.findByBookId(book.getId());

        assertThat(comments)
                .isNotEmpty()
                .hasSize(1) // More specific assertion since we cleared comments in setUp
                .first()
                .satisfies(c -> {
                    assertThat(c.getMessage()).isEqualTo(TEST_COMMENT_MESSAGE);
                    assertThat(c.getBook().getId()).isEqualTo(book.getId());
                });
    }

    @Test
    @DisplayName("Should update saved comment")
    void shouldUpdateComment() {
        Comment comment = createTestComment();
        Comment savedComment = mongoTemplate.save(comment);

        // Verify initial state
        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getMessage()).isEqualTo(TEST_COMMENT_MESSAGE);

        // Update the comment
        savedComment.setMessage(UPDATED_COMMENT_MESSAGE);
        Comment updatedComment = commentRepository.save(savedComment);

        // Verify update
        assertThat(updatedComment.getId()).isEqualTo(savedComment.getId()); // ID should remain the same
        assertThat(updatedComment.getMessage()).isEqualTo(UPDATED_COMMENT_MESSAGE);
        assertThat(updatedComment.getBook().getId()).isEqualTo(book.getId());

        // Verify persistence of update
        Comment commentFromDB = mongoTemplate.findOne(
                Query.query(Criteria.where("_id").is(updatedComment.getId())), Comment.class);
        assertThat(commentFromDB)
                .as("Updated comment should be found in database")
                .isNotNull();
        assertThat(commentFromDB.getMessage()).isEqualTo(UPDATED_COMMENT_MESSAGE);
    }

    @Test
    @DisplayName("Should handle empty results when no comments exist for book")
    void shouldReturnEmptyListWhenNoCommentsForBook() {
        // This test assumes setUp() has cleared all comments
        List<Comment> comments = commentRepository.findByBookId(book.getId());
        assertThat(comments).isEmpty();
    }

    private Comment createTestComment() {
        Comment comment = new Comment();
        comment.setMessage(TEST_COMMENT_MESSAGE);
        comment.setBook(book);
        return comment;
    }
}