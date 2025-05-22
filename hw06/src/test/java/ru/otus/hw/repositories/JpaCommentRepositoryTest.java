package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaCommentRepository.class)
@DisplayName("JpaCommentRepository Test")
class JpaCommentRepositoryTest {
    private static final long FIRST_BOOK_ID = 1L;
    private static final String COMMENT_MESSAGE = "Great book!";
    private static final String UPDATED_MESSAGE = "Updated message";

    @Autowired
    private JpaCommentRepository jpaCommentRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("Should save comment")
    void shouldSaveComment() {
        var book = getBookById(FIRST_BOOK_ID);
        var comment = createComment(COMMENT_MESSAGE, book);
        var savedComment = jpaCommentRepository.save(comment);
        em.flush();

        var foundComment = em.find(Comment.class, savedComment.getId());
        assertThat(foundComment.getId()).isNotNull();
        assertThat(foundComment.getMessage()).isEqualTo(COMMENT_MESSAGE);
        assertThat(foundComment.getBook()).isEqualTo(book);
    }

    @Test
    @DisplayName("Should find comments by book ID")
    void shouldFindCommentsByBookId() {
        var book = getBookById(FIRST_BOOK_ID);
        var comment = createComment(COMMENT_MESSAGE, book);
        var savedComment = jpaCommentRepository.save(comment);
        em.flush();

        var comments = jpaCommentRepository.findByBookId(book.getId());
        assertThat(comments)
                .isNotNull()
                .isNotEmpty()
                .allMatch(c -> c.getMessage() != null && !c.getMessage().isEmpty())
                .allMatch(c -> c.getBook().getId() == book.getId())
                .contains(savedComment);
    }

    @Test
    @DisplayName("Should update saved comment")
    void shouldUpdateComment() {
        // Step 1: Get the book by its ID
        var book = getBookById(FIRST_BOOK_ID);

        // Step 2: Create and save a new comment
        var comment = createComment(COMMENT_MESSAGE, book);
        var savedComment = jpaCommentRepository.save(comment);

        // Step 3: Update the saved comment's message
        savedComment.setMessage(UPDATED_MESSAGE);

        // Step 4: Save the updated comment (only once)
        jpaCommentRepository.save(savedComment);
        em.flush();

        // Step 5: Verify if the message was updated
        var foundComment = em.find(Comment.class, savedComment.getId());
        assertThat(foundComment.getMessage()).isEqualTo(UPDATED_MESSAGE);
    }

    private Book getBookById(long bookId) {
        var book = em.find(Book.class, bookId);
        assertThat(book)
                .as("Book with ID %s not found", bookId)
                .isNotNull();
        return book;
    }

    private Comment createComment(String message, Book book) {
        var comment = new Comment();
        comment.setMessage(message);
        comment.setBook(book);
        return comment;
    }
}
