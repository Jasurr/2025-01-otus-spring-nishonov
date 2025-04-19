package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.models.Book;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Tests for CommentService")
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BookService bookService;

    private Optional<Book> testBook;

    @BeforeEach
    @DisplayName("Set up a test book")
    void setUp() {
        // Fetch all books and select the first one
        var books = bookService.findAll();
        if (books.isEmpty()) {
            fail("At least one book must exist for testing");
        }
        testBook = Optional.of(books.get(0));
    }
    @Test
    @DisplayName("Find comments by book ID should return a non-empty list")
    void findCommentsByBookIdShouldReturnNonEmptyList() {
        // Insert a test comment
        var savedComment = commentService.insert("Test comment", testBook.get().getId());
        // Fetch comments for the book
        var comments = commentService.findByBookId(testBook.get().getId());

        // Verify the comment list is not empty and contains the saved comment
        assertThat(comments)
                .isNotEmpty()
                .anyMatch(comment -> comment.getId() == savedComment.getId());
    }

    @Test
    @DisplayName("Insert comment should save correctly")
    void insertCommentShouldSaveCorrectly() {
        // Insert a test comment
        var savedComment = commentService.insert("Test comment", testBook.get().getId());

        // Verify the saved comment has correct properties
        assertThat(savedComment)
                .isNotNull()
                .hasFieldOrPropertyWithValue("message", "Test comment")
                .extracting(comment -> comment.getBook().getId())
                .isEqualTo(testBook.get().getId());
    }

    @Test
    @DisplayName("Update comment should change the message")
    void updateCommentShouldChangeMessage() {
        // Insert a test comment
        var savedComment = commentService.insert("Test comment", testBook.get().getId());
        // Update the comment with a new message
        var updatedComment = commentService.update(savedComment.getId(), "Updated comment", testBook.get().getId());

        // Verify the updated comment has the new message and correct book ID
        assertThat(updatedComment)
                .isNotNull()
                .hasFieldOrPropertyWithValue("message", "Updated comment")
                .extracting(comment -> comment.getBook().getId())
                .isEqualTo(testBook.get().getId());
    }

    @Test
    @DirtiesContext
    @DisplayName("Delete comment should remove it from the database")
    void deleteCommentShouldRemoveFromDatabase() {
        // Insert a test comment
        var savedComment = commentService.insert("Test comment", testBook.get().getId());
        // Delete the comment
        commentService.deleteById(savedComment.getId());

        // Verify the comment no longer exists
        var deletedComment = commentService.findById(savedComment.getId());
        assertThat(deletedComment).isEmpty();
    }

}