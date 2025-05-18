package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.services.GenreService;

@RestController
@RequestMapping("/api/v1/genres")
@RequiredArgsConstructor
public class GenreRestController {
    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<?> getAllGenres() {
        var genres = genreService.findAll();
        return ResponseEntity.ok(genres);
    }

}
