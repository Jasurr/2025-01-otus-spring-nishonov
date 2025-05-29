package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommentService commentService;

    @Test
    void shouldReturnCommentsPageByBookId() throws Exception {
        List<CommentDto> comments = List.of(
                new CommentDto(1L, "First comment"),
                new CommentDto(2L, "Second comment")
        );

        when(commentService.findByBookId(1L)).thenReturn(comments);

        mvc.perform(get("/book/comment/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("comment/list"))
                .andExpect(model().attribute("comments", comments))
                .andExpect(model().attribute("bookId", 1L));
    }

    @Test
    void shouldAddCommentAndRedirect() throws Exception {
        CommentDto comment = new CommentDto(1L, "New comment");

        when(commentService.insert("New comment", 1L)).thenReturn(comment);

        mvc.perform(post("/book/comment/1/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("message", "New comment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book/comment/1"));

        verify(commentService).insert("New comment", 1L);
    }

    @Test
    void shouldShowEditCommentForm() throws Exception {
        long commentId = 1L;
        long bookId = 1L;
        CommentDto comment = new CommentDto(commentId, "Comment to edit");

        when(commentService.findById(commentId)).thenReturn(Optional.of(comment));

        mvc.perform(get("/book/comment/{commentId}/edit", commentId)
                        .param("bookId", String.valueOf(bookId)))
                .andExpect(status().isOk())
                .andExpect(view().name("comment/edit"))
                .andExpect(model().attribute("comment", comment))
                .andExpect(model().attribute("bookId", bookId));
    }

    @Test
    void shouldHandleEditCommentNotFound() throws Exception {
        long commentId = 999L;
        long bookId = 1L;

        when(commentService.findById(commentId)).thenReturn(Optional.empty());

        mvc.perform(get("/book/comment/{commentId}/edit", commentId)
                        .param("bookId", String.valueOf(bookId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book/comment/1?error=Comment+not+found"));
    }

    @Test
    void shouldUpdateCommentAndRedirect() throws Exception {
        long commentId = 1L;
        long bookId = 1L;
        CommentDto updatedComment = new CommentDto(commentId, "Updated message");

        when(commentService.update(commentId, "Updated message")).thenReturn(updatedComment);

        mvc.perform(post("/book/comment/{commentId}/update", commentId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("message", "Updated message")
                        .param("bookId", String.valueOf(bookId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book/comment/1"));

        verify(commentService).update(commentId, "Updated message");
    }

    @Test
    void shouldDeleteCommentAndRedirect() throws Exception {
        long commentId = 1L;
        long bookId = 1L;

        doNothing().when(commentService).deleteById(commentId);

        mvc.perform(post("/book/comment/{commentId}/delete", commentId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("bookId", String.valueOf(bookId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book/comment/1"));

        verify(commentService).deleteById(commentId);
    }

    @Test
    void shouldHandleUpdateCommentError() throws Exception {
        long commentId = 1L;
        long bookId = 1L;

        when(commentService.update(commentId, "Updated message"))
                .thenThrow(new RuntimeException("Update failed"));

        mvc.perform(post("/book/comment/{commentId}/update", commentId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("message", "Updated message")
                        .param("bookId", String.valueOf(bookId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book/comment/1?error=Failed+to+update+comment"));
    }

    @Test
    void shouldHandleDeleteCommentError() throws Exception {
        long commentId = 1L;
        long bookId = 1L;

        doThrow(new RuntimeException("Delete failed"))
                .when(commentService).deleteById(commentId);

        mvc.perform(post("/book/comment/{commentId}/delete", commentId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("bookId", String.valueOf(bookId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book/comment/1?error=Failed+to+delete+comment"));
    }
}