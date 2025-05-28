package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
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

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Book book;

    @BeforeEach
    void setUp() {
        // Fetch a Book from the migrated data
        book = bookRepository.findAll().get(0);
    }

    @Test
    @DisplayName("Should save comment")
    void shouldSaveComment() {
        Comment comment = createTestComment();
        Comment savedComment = commentRepository.save(comment);

        assertThat(savedComment.getId()).isNotNull().isNotBlank();
        assertThat(savedComment.getMessage()).isEqualTo(TEST_COMMENT_MESSAGE);
        assertThat(savedComment.getBook()).isEqualTo(book);
    }

    @Test
    @DisplayName("Should find comments by book ID")
    void shouldFindByBookId() {
        Comment comment = createTestComment();
        commentRepository.save(comment);

        List<Comment> comments = commentRepository.findByBookId(book.getId());

        assertThat(comments)
                .isNotEmpty()
                .anyMatch(c -> c.getMessage().equals(TEST_COMMENT_MESSAGE));
    }

    @Test
    @DisplayName("Should update saved comment")
    void shouldUpdateComment() {
        Comment comment = createTestComment();
        Comment savedComment = commentRepository.save(comment);
        savedComment.setMessage(UPDATED_COMMENT_MESSAGE);
        Comment updatedComment = commentRepository.save(savedComment);

        assertThat(updatedComment.getMessage()).isEqualTo(UPDATED_COMMENT_MESSAGE);
        assertThat(updatedComment.getBook()).isEqualTo(book);
    }

    private Comment createTestComment() {
        Comment comment = new Comment();
        comment.setMessage(TEST_COMMENT_MESSAGE);
        comment.setBook(book);
        return comment;
    }
}