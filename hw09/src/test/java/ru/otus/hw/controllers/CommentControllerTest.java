package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        List<CommentDto> comments = List.of(
                new CommentDto(1L, "First comment"),
                new CommentDto(2L, "Second comment")
        );

        when(commentService.findByBookId(1L)).thenReturn(comments);

        mvc.perform(get("/book/comment/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(comments)));
    }

    @Test
    void shouldAddComment() throws Exception {
        CommentDto comment = new CommentDto(1L, "New comment");

        when(commentService.insert("New comment", 1L)).thenReturn(comment);

        mvc.perform(post("/book/comment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateComment() throws Exception {
        long commentId = 1L;
        CommentDto comment = new CommentDto(commentId, "Updated message");

        when(commentService.update(commentId, "Updated message")).thenReturn(comment);

        mvc.perform(put("/book/comment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk());
    }



    @Test
    void shouldDeleteComment() throws Exception {
        long commentId = 1L;
        CommentDto comment = new CommentDto(commentId, "New comment");

        when(commentService.findById(commentId)).thenReturn(Optional.of(comment));

        mvc.perform(delete("/book/comment/" + commentId))
                .andExpect(status().isOk());
    }

}