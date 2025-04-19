package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookRepository bookRepository;

    @DisplayName("should save comment")
    @Test
    void shouldSaveComment() {
        Book book = bookRepository.findById(1L).orElseThrow();
        Comment comment = new Comment(0, "Great book!", book);
        Comment savedComment = commentRepository.save(comment);
        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getMessage()).isEqualTo("Great book!");
        assertThat(savedComment.getBook()).isEqualTo(book);
    }

    @DisplayName("should find comment by id")
    @Test
    void shouldFindByBookId() {
        Book book = bookRepository.findById(1L).orElseThrow();
        Comment comment = new Comment(0, "Great book!", book);
        commentRepository.save(comment);
        List<Comment> comments = commentRepository.findByBookId(book.getId());
        assertThat(comments).isNotEmpty();
        assertThat(comments.stream().filter(c -> c.getMessage().equals("Great book")).count());
    }

    @DisplayName("should update saved comment")
    @Test
    void shouldUpdateComment() {
        Book book = bookRepository.findById(1L).orElseThrow();
        Comment comment = new Comment(0, "Great book!", book);
        Comment savedComment = commentRepository.save(comment);
        savedComment.setMessage("Updated message");
        Comment updatedComment = commentRepository.save(savedComment);
        assertThat(updatedComment.getMessage()).isEqualTo("Updated message");
    }
}