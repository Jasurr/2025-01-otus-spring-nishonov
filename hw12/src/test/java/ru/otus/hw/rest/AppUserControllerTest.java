package ru.otus.hw.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AppUserDto;
import ru.otus.hw.dto.RoleDto;
import ru.otus.hw.models.Role;
import ru.otus.hw.security.JwtUtil;
import ru.otus.hw.services.AppUserService;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppUserController.class)
class AppUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppUserService appUserService;

    @MockBean
    private JwtUtil jwtUtil;

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role(1L, "ADMIN");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminCanAccessUsersEndpoint() throws Exception {
        when(appUserService.findAll()).thenReturn(List.of(
                new AppUserDto(1L, "admin", Set.of(new RoleDto(1L, "ADMIN"))),
                new AppUserDto(2L, "user", Set.of(new RoleDto(2L, "USER")))
        ));

        mockMvc.perform(get("/api/v1/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[1].username").value("user"));
    }
}
