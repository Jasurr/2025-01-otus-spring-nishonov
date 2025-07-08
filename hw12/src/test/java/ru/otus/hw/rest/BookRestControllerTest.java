package ru.otus.hw.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.security.JwtUtil;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookRestController.class)
class BookRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllBooks() throws Exception {
        // Arrange
        List<BookDto> books = List.of(
                new BookDto(1L, "Book One", new AuthorDto(1L, "Author One"), List.of(new GenreDto(1L, "Fiction"))),
                new BookDto(2L, "Book Two", new AuthorDto(2L, "Author Two"), List.of(new GenreDto(2L, "Non-Fiction")))
        );
        given(bookService.findAll()).willReturn(books);

        // Act & Assert
        mockMvc.perform(get("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Book One"))
                .andExpect(jsonPath("$[0].author.id").value(1))
                .andExpect(jsonPath("$[0].genres[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Book Two"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetBookByIdFound() throws Exception {
        // Arrange
        BookDto book = new BookDto(1L, "Book One", new AuthorDto(1L, "Author One"),
                List.of(new GenreDto(1L, "Fiction")));
        given(bookService.findById(1L)).willReturn(Optional.of(book));

        // Act & Assert
        mockMvc.perform(get("/api/v1/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Book One"))
                .andExpect(jsonPath("$.author.id").value(1))
                .andExpect(jsonPath("$.genres[0].id").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetBookByIdNotFound() throws Exception {
        // Arrange
        given(bookService.findById(1L)).willReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSaveNewBook() throws Exception {
        // Arrange
        BookDto inputDto = new BookDto(null, "New Book", new AuthorDto(1L, "Author One"),
                List.of(new GenreDto(1L, "Fiction")));
        BookDto savedDto = new BookDto(1L, "New Book", new AuthorDto(1L, "Author One"),
                List.of(new GenreDto(1L, "Fiction")));
        given(bookService.insert(anyString(), anyLong(), anySet())).willReturn(savedDto);

        // Act & Assert
        mockMvc.perform(post("/api/v1/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Book"))
                .andExpect(jsonPath("$.author.id").value(1))
                .andExpect(jsonPath("$.genres[0].id").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateBook() throws Exception {
        // Arrange
        BookDto inputDto = new BookDto(1L, "Updated Book", new AuthorDto(1L, "Author One"),
                List.of(new GenreDto(1L, "Fiction")));
        BookDto updatedDto = new BookDto(1L, "Updated Book", new AuthorDto(1L, "Author One"),
                List.of(new GenreDto(1L, "Fiction")));
        given(bookService.update(anyLong(), anyString(), anyLong(), anySet())).willReturn(updatedDto);

        // Act & Assert
        mockMvc.perform(put("/api/v1/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Book"))
                .andExpect(jsonPath("$.author.id").value(1))
                .andExpect(jsonPath("$.genres[0].id").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteBook() throws Exception {
        // Arrange
        doNothing().when(bookService).deleteById(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/books/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookService).deleteById(1L);
    }
}