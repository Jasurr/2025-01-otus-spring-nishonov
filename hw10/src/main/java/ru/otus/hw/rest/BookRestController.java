package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.rest.dto.BookDto;
import ru.otus.hw.services.BookService;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookRestController {
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<?> getAllBooks() {
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

}
