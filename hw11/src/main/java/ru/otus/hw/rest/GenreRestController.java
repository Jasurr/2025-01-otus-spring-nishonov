package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

@RestController
@RequiredArgsConstructor
public class GenreRestController {
    private final GenreService genreService;

    @GetMapping("/api/v1/genres")
    public Flux<GenreDto> getAllGenres() {
        return genreService.findAll();
    }
}
