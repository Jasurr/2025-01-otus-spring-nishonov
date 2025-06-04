package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.models.Book;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Tests for CommentService")
class CommentServiceTest {

    private static final String COMMENT_MESSAGE = "Test comment";

    private static final String UPDATED_COMMENT_MESSAGE = "Updated comment";

    @Autowired
    private CommentService commentService;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = getAnyBook();
        assertThat(testBook).isNotNull();
    }

    @Test
    @DisplayName("Should find comment by ID")
    void shouldFindCommentById() {
        var savedComment = commentService.insert(COMMENT_MESSAGE, testBook.getId());
        var foundComment = commentService.findById(savedComment.id());

        assertThat(foundComment)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("message", COMMENT_MESSAGE);
    }

    @Test
    @DisplayName("Should find comments by book ID")
    void shouldFindCommentsByBookId() {
        var savedComment = commentService.insert(COMMENT_MESSAGE, testBook.getId());
        var comments = commentService.findByBookId(testBook.getId());

        assertThat(comments)
                .isNotNull()
                .isNotEmpty()
                .allMatch(comment -> comment.message() != null && !comment.message().isEmpty())
                .anyMatch(comment -> comment.id().equals(savedComment.id()) && comment.message().equals(COMMENT_MESSAGE));
    }

    @Test
    @DisplayName("Should insert comment")
    void shouldInsertComment() {
        var savedComment = commentService.insert(COMMENT_MESSAGE, testBook.getId());

        assertThat(savedComment)
                .isNotNull()
                .hasFieldOrPropertyWithValue("message", COMMENT_MESSAGE);
    }

    @Test
    @DisplayName("Should update comment")
    void shouldUpdateComment() {
        var savedComment = commentService.insert(COMMENT_MESSAGE, testBook.getId());
        var updatedComment = commentService.update(savedComment.id(), UPDATED_COMMENT_MESSAGE);

        assertThat(updatedComment)
                .isNotNull()
                .hasFieldOrPropertyWithValue("message", UPDATED_COMMENT_MESSAGE);
    }

    @Test
    @DisplayName("Should delete comment")
    void shouldDeleteComment() {
        var savedComment = commentService.insert(COMMENT_MESSAGE, testBook.getId());
        commentService.deleteById(savedComment.id());

        var deletedComment = commentService.findById(savedComment.id());
        assertThat(deletedComment).isEmpty();
    }

    private Book getAnyBook() {
        return mongoTemplate.findAll(Book.class).stream()
                .filter(b -> b.getId() != null)
                .findFirst()
                .orElse(null);
    }
}
