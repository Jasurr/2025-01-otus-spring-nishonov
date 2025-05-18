package ru.otus.hw.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DisplayName("Tests for CommentService")
@Import(CommentServiceImpl.class)
class CommentServiceTest {

    private static final long FIRST_BOOK_ID = 1L;
    private static final String COMMENT_MESSAGE = "Test comment";
    private static final String UPDATED_COMMENT_MESSAGE = "Updated comment";

    @Autowired
    private CommentService commentService;

    @PersistenceContext
    private EntityManager em;

    private Book testBook;

    @BeforeEach
    @DisplayName("Set up a test book")
    void setUp() {
        testBook = getBookById(FIRST_BOOK_ID);
    }

    @Test
    @DisplayName("Should find comment by ID without LazyInitializationException")
    void shouldFindCommentById() {
        var savedComment = commentService.insert(COMMENT_MESSAGE, testBook.getId());
        var foundComment = commentService.findById(savedComment.getId());

        assertThat(foundComment)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("message", COMMENT_MESSAGE)
                .hasFieldOrPropertyWithValue("bookId", testBook.getId());
    }

    @Test
    @DisplayName("Should find comments by book ID without LazyInitializationException")
    void shouldFindCommentsByBookId() {
        var savedComment = commentService.insert(COMMENT_MESSAGE, testBook.getId());
        var comments = commentService.findByBookId(testBook.getId());

        assertThat(comments)
                .isNotNull()
                .isNotEmpty()
                .allMatch(comment -> comment.getMessage() != null && !comment.getMessage().isEmpty())
                .allMatch(comment -> comment.getBookId() == testBook.getId())
                .anyMatch(comment -> comment.getId() == savedComment.getId() && comment.getMessage().equals(COMMENT_MESSAGE));
    }

    @Test
    @DisplayName("Should insert comment correctly")
    void shouldInsertComment() {
        var savedComment = commentService.insert(COMMENT_MESSAGE, testBook.getId());

        assertThat(savedComment)
                .isNotNull()
                .hasFieldOrPropertyWithValue("message", COMMENT_MESSAGE)
                .extracting(comment -> comment.getBookId())
                .isEqualTo(testBook.getId());
    }

    @Test
    @DisplayName("Should update comment message")
    void shouldUpdateComment() {
        var savedComment = commentService.insert(COMMENT_MESSAGE, testBook.getId());
        var updatedComment = commentService.update(savedComment.getId(), UPDATED_COMMENT_MESSAGE, testBook.getId());

        assertThat(updatedComment)
                .isNotNull()
                .hasFieldOrPropertyWithValue("message", UPDATED_COMMENT_MESSAGE)
                .extracting(comment -> comment.getBookId())
                .isEqualTo(testBook.getId());
    }

    @Test
    @DisplayName("Should delete comment from database")
    void shouldDeleteComment() {
        var savedComment = commentService.insert(COMMENT_MESSAGE, testBook.getId());
        commentService.deleteById(savedComment.getId());

        var deletedComment = commentService.findById(savedComment.getId());
        assertThat(deletedComment).isEmpty();
    }

    @Transactional(readOnly = true)
    protected Book getBookById(long bookId) {
        var book = em.find(Book.class, bookId);
        assertNotNull(book, "Book with ID " + bookId + " not found");
        return book;
    }

}