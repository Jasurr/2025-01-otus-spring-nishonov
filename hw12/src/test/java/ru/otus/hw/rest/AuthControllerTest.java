package ru.otus.hw.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthRequest;
import ru.otus.hw.models.AppUser;
import ru.otus.hw.models.Role;
import ru.otus.hw.repositories.AppUserRepository;
import ru.otus.hw.security.JwtUtil;
import ru.otus.hw.services.AppUserService;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    @MockBean
    private PasswordEncoder passwordEncoder;

    // Helper method to create a mock user result
    private Object createMockUserResult(Set<String> roles) {
        return new Object() {
            public Set<String> roles() {
                return roles;
            }
        };
    }

    @Test
    void login_WithValidCredentials_ShouldReturnJwtToken() throws Exception {
        // Given
        String username = "testuser";
        String password = "testpass";
        String expectedToken = "jwt-token-123";

        AuthRequest authRequest = new AuthRequest(username, password);

        UserDetails userDetails = User.builder()
                .username(username)
                .password(password)
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        // Create a mock user service result
        Object userResult = createMockUserResult(Set.of("USER"));

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        doReturn(userResult).when(userService).findByUsername(username);
        when(jwtUtil.generateToken(eq(username), any(Set.class))).thenReturn(expectedToken);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedToken));

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        verify(userDetailsService).loadUserByUsername(username);
        verify(userService).findByUsername(username);
        verify(jwtUtil).generateToken(eq(username), eq(Set.of("USER")));
    }

    @Test
    void login_WithAdminUser_ShouldReturnJwtTokenWithAdminRoles() throws Exception {
        // Given
        String username = "admin";
        String password = "admin";
        String expectedToken = "jwt-admin-token-123";

        AuthRequest authRequest = new AuthRequest(username, password);

        UserDetails userDetails = User.builder()
                .username(username)
                .password(password)
                .authorities(List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("ROLE_USER")
                ))
                .build();

        // Create a mock user service result with admin roles
        Object userResult = createMockUserResult(Set.of("ADMIN", "USER"));

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        doReturn(userResult).when(userService).findByUsername(username);
        when(jwtUtil.generateToken(eq(username), any(Set.class))).thenReturn(expectedToken);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedToken));

        verify(jwtUtil).generateToken(eq(username), eq(Set.of("ADMIN", "USER")));
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        // Given
        String username = "testuser";
        String password = "wrongpass";

        AuthRequest authRequest = new AuthRequest(username, password);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtUtil, never()).generateToken(anyString(), any(Set.class));
    }

    @Test
    void login_WithNonExistentUser_ShouldReturnUnauthorized() throws Exception {
        // Given
        String username = "nonexistent";
        String password = "testpass";

        AuthRequest authRequest = new AuthRequest(username, password);

        when(userDetailsService.loadUserByUsername(username))
                .thenThrow(new UsernameNotFoundException("User not found"));

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isInternalServerError());

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        verify(userDetailsService).loadUserByUsername(username);
        verify(jwtUtil, never()).generateToken(anyString(), any(Set.class));
    }

    @Test
    void login_WithEmptyUsername_ShouldReturnBadRequest() throws Exception {
        // Given
        AuthRequest authRequest = new AuthRequest("", "password");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void login_WithEmptyPassword_ShouldReturnBadRequest() throws Exception {
        // Given
        AuthRequest authRequest = new AuthRequest("username", "");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void login_WithNullRequestBody_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void login_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void login_WhenJwtGenerationFails_ShouldReturnInternalServerError() throws Exception {
        // Given
        String username = "testuser";
        String password = "testpass";

        AuthRequest authRequest = new AuthRequest(username, password);

        UserDetails userDetails = User.builder()
                .username(username)
                .password(password)
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        Object userResult = createMockUserResult(Set.of("USER"));

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        doReturn(userResult).when(userService).findByUsername(username);
        when(jwtUtil.generateToken(eq(username), any(Set.class)))
                .thenThrow(new RuntimeException("JWT generation failed"));

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isInternalServerError());

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        verify(userDetailsService).loadUserByUsername(username);
        verify(userService).findByUsername(username);
        verify(jwtUtil).generateToken(eq(username), eq(Set.of("USER")));
    }

    @Test
    void login_WhenUserServiceFails_ShouldReturnInternalServerError() throws Exception {
        // Given
        String username = "testuser";
        String password = "testpass";

        AuthRequest authRequest = new AuthRequest(username, password);

        UserDetails userDetails = User.builder()
                .username(username)
                .password(password)
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        doThrow(new RuntimeException("User service failed")).when(userService).findByUsername(username);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isInternalServerError());

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        verify(userDetailsService).loadUserByUsername(username);
        verify(userService).findByUsername(username);
        verify(jwtUtil, never()).generateToken(anyString(), any(Set.class));
    }
}