package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.BookService;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class BookRestController {
    private final BookService bookService;

    @GetMapping("/api/v1/books")
    public Flux<BookDto> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/api/v1/books/{id}")
    public Mono<BookDto> getBookById(@PathVariable("id") String bookId) {
        return bookService.findById(bookId);
    }

    @PostMapping("/api/v1/books")
    public Mono<BookDto> saveNewBook(@RequestBody BookDto dto) {
        var genreIds = dto.genres()
                .stream()
                .map(GenreDto::id)
                .collect(Collectors.toSet());
        return bookService.insert(dto.title(), dto.author().id(), genreIds);
    }

    @PutMapping("/api/v1/books")
    public Mono<BookDto> updateBook(@RequestBody BookDto dto) {
        var genreIds = dto.genres()
                .stream()
                .map(GenreDto::id)
                .collect(Collectors.toSet());
        return bookService.update(dto.id(), dto.title(), dto.author().id(), genreIds);

    }

    @DeleteMapping("/api/v1/books/{id}")
    public Mono<Void> deleteBook(@PathVariable String id) {
        return bookService.deleteById(id);
    }
}
