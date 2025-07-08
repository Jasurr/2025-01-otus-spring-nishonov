package ru.otus.hw.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AppUserDto;
import ru.otus.hw.dto.AuthRequest;
import ru.otus.hw.repositories.AppUserRepository;
import ru.otus.hw.security.JwtUtil;
import ru.otus.hw.services.AppUserService;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AppUserService userService;

    @MockBean
    private AppUserRepository appUserRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private PasswordEncoder passwordEncoder;


    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    void testLoginSuccess() throws Exception {
        // given
        AuthRequest authRequest = new AuthRequest("user", "password");
        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("user")
                .password("password")
                .roles("USER")
                .build();

        AppUserDto appUser = new AppUserDto(
                1L,
                "user",
                Set.of("USER")
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("user", "password"));

        when(userDetailsService.loadUserByUsername("user")).thenReturn(mockUserDetails);
        when(userService.findByUsername("user")).thenReturn(appUser);
        when(jwtUtil.generateToken("user", Set.of("USER"))).thenReturn("mock-jwt-token");

        // when - then
        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("mock-jwt-token"));
    }

}
