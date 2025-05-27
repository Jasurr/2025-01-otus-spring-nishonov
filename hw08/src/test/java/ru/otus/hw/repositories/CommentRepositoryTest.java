package ru.otus.hw.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class CommentRepositoryTest {
    private static final String TEST_COMMENT_MESSAGE = "Test comment";
    private static final String UPDATED_COMMENT_MESSAGE = "Updated comment";

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Book book;

    @BeforeEach
    void setUp() {
        book = mongoTemplate.findAll(Book.class).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    @Test
    @DisplayName("Should save comment")
    void shouldSaveComment() {
        Comment comment = createTestComment();
        Comment savedComment = commentRepository.save(comment);
        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getMessage()).isEqualTo(TEST_COMMENT_MESSAGE);
        assertThat(savedComment.getBook()).isEqualTo(book.getId());
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
    }

    private Comment createTestComment() {
        Comment comment = new Comment();
        comment.setMessage(TEST_COMMENT_MESSAGE);
        comment.setBook(book.getId());
        return comment;
    }
}
