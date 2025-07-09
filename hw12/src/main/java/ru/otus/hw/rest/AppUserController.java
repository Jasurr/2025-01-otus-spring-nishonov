package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.AppUserDto;
import ru.otus.hw.services.AppUserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    @GetMapping("api/v1/users")
    public ResponseEntity<List<AppUserDto>> getAll() {
        return ResponseEntity.ok(appUserService.findAll());
    }
}
