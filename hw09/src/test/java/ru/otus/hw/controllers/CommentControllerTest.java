package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.CommentDTO;
import ru.otus.hw.services.CommentService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CommentController.class)
class CommentControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnCommentsByBookId() throws Exception {
        List<CommentDTO> comments = List.of(
                new CommentDTO(1L, "Comment 1", 1L),
                new CommentDTO(2L, "Comment 2", 1L)
        );

        when(commentService.findByBookId(1L)).thenReturn(comments);

        mvc.perform(get("/book/comment/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(comments)));
    }

    @Test
    void shouldAddComment() throws Exception {
        CommentDTO comment = new CommentDTO(1L, "New comment", 1L);

        when(commentService.insert("New comment", 1L)).thenReturn(comment);

        mvc.perform(post("/book/comment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateComment() throws Exception {
        long commentId = 1L;
        long bookId = 1L;
        CommentDTO comment = new CommentDTO(commentId, "Updated message", bookId);

        when(commentService.update(commentId, "Updated message", bookId)).thenReturn(comment);

        mvc.perform(put("/book/comment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk());
    }



    @Test
    void shouldDeleteComment() throws Exception {
        long commentId = 1L;
        CommentDTO comment = new CommentDTO(commentId, "New comment", 1L);

        when(commentService.findById(commentId)).thenReturn(Optional.of(comment));

        mvc.perform(delete("/book/comment/" + commentId))
                .andExpect(status().isOk());
    }

}