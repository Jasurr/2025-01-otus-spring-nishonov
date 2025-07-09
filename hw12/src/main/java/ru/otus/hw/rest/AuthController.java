package ru.otus.hw.rest;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.AuthRequest;
import ru.otus.hw.dto.RoleDto;
import ru.otus.hw.security.JwtUtil;
import ru.otus.hw.services.AppUserService;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    private final AppUserService userService;

    @PostMapping("/api/v1/auth/login")
    public String login(@RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password())
        );
        var user = userService.findByUsername(authRequest.username());

        return jwtUtil.generateToken(
                user.username(),
                user
                        .roles()
                        .stream()
                        .map(RoleDto::name)
                        .collect(Collectors.toSet()));
    }
}
