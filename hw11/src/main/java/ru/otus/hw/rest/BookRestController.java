package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class BookRestController {
    private final BookService bookService;

    @GetMapping("/api/v1/books")
    public ResponseEntity<List<BookDto>> getAllBooks() {
        var books = bookService.findAll();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/api/v1/books/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable("id") long bookId) {
        var book = bookService.findById(bookId);
        return book.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api/v1/books/add")
    public ResponseEntity<BookDto> saveNewBook(@RequestBody BookDto dto) {
        var genreIds = dto.genres()
                .stream()
                .map(GenreDto::id)
                .collect(Collectors.toSet());
        var savedBook = bookService.insert(dto.title(), dto.author().id(), genreIds);
        return ResponseEntity.ok(savedBook);
    }

    @PutMapping("/api/v1/books/update")
    public ResponseEntity<BookDto> updateBook(@RequestBody BookDto dto) {
        var genreIds = dto.genres()
                .stream()
                .map(GenreDto::id)
                .collect(Collectors.toSet());
        bookService.update(dto.id(), dto.title(), dto.author().id(), genreIds);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/api/v1/books/delete/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
    }
}
