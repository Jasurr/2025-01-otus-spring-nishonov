package ru.otus.hw.rest;

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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CommentRestController.class)
class CommentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Test
    void testGetCommentFound() throws Exception {
        // Arrange
        CommentDto comment = new CommentDto(1L, "Great book!");
        given(commentService.findById(1L)).willReturn(Optional.of(comment));

        // Act & Assert
        mockMvc.perform(get("/api/v1/book/comments/one/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.message").value("Great book!"));
    }

    @Test
    void testGetCommentNotFound() throws Exception {
        // Arrange
        given(commentService.findById(1L)).willReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/book/comments/one/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCommentsByBookId() throws Exception {
        // Arrange
        List<CommentDto> comments = List.of(
                new CommentDto(1L, "Great book!"),
                new CommentDto(2L, "Really enjoyed it!")
        );
        given(commentService.findByBookId(1L)).willReturn(comments);

        // Act & Assert
        mockMvc.perform(get("/api/v1/book/comments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].message").value("Great book!"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].message").value("Really enjoyed it!"));
    }

    @Test
    void testAddComment() throws Exception {
        // Arrange
        CommentDto newComment = new CommentDto(1L, "Nice book!");
        given(commentService.insert(anyString(), anyLong())).willReturn(newComment);

        // Act & Assert
        mockMvc.perform(post("/api/v1/book/comments/1/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("message", "Nice book!"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateComment() throws Exception {
        // Arrange
        CommentDto updatedComment = new CommentDto(1L, "Updated comment");
        given(commentService.update(anyLong(), anyString())).willReturn(updatedComment);

        // Act & Assert
        mockMvc.perform(put("/api/v1/book/comments/1/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("message", "Updated comment"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteComment() throws Exception {
        // Arrange
        doNothing().when(commentService).deleteById(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/book/comments/1/delete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}