package ru.otus.hw.rest;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.AuthRequest;
import ru.otus.hw.models.AppUser;
import ru.otus.hw.models.Role;
import ru.otus.hw.repositories.AppUserRepository;
import ru.otus.hw.security.JwtUtil;
import ru.otus.hw.services.AppUserService;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;


    private final UserDetailsService userDetailsService;

    private final JwtUtil jwtUtil;

    private final AppUserService userService;

    private final AppUserRepository appUserRepository;

    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    private void init() {
        var admin = new AppUser();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRoles(Set.of(Role.ADMIN, Role.USER));
        var user = new AppUser();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("user"));
        user.setRoles(Set.of(Role.USER));
        appUserRepository.saveAll(Set.of(admin, user));
    }

    @PostMapping("api/v1/auth/login")
    public String login(@RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password())
        );
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.username());
        Set<String> roles = new HashSet<>(userService.findByUsername(userDetails.getUsername()).roles());
        return jwtUtil.generateToken(
                userDetails.getUsername(),
                roles
        );
    }
}
