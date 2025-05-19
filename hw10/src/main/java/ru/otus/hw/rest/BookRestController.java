package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.hw.models.Genre;
import ru.otus.hw.rest.dto.BookDto;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookRestController {
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        var books = bookService.findAll();
        var booksDto = books.stream()
                .map(book -> {
                    var bookDto = new BookDto();
                    bookDto.setId(book.getId());
                    bookDto.setTitle(book.getTitle());
                    bookDto.setAuthor(book.getAuthor());
                    bookDto.setGenres(book.getGenres());
                    return bookDto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(booksDto);
    }

    @GetMapping("{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable("id") long bookId) {
        var book = bookService.findById(bookId).get();
        return ResponseEntity.ok(book);
    }

    @PostMapping("/add")
    public ResponseEntity<BookDto> saveNewBook(@RequestBody BookDto dto) {
        var genreIds = dto.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        dto = bookService.insert(dto.getTitle(), dto.getAuthor().getId(), genreIds);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/edit")
    public ResponseEntity<BookDto> updateBook(@RequestBody BookDto dto) {
        var genreIds = dto.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        bookService.update(dto.getId(), dto.getTitle(), dto.getAuthor().getId(), genreIds);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
    }
}
