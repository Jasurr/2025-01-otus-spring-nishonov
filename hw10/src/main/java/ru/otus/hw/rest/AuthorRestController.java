package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.services.AuthorService;

@RestController
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
public class AuthorRestController {
    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<?> getAllAuthors() {
        var authors = authorService.findAll();
        return ResponseEntity.ok(authors);
    }
}
