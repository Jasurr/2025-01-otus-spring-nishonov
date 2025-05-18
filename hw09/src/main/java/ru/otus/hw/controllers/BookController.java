package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.models.Book;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    @GetMapping
    public String getAllBooks(Model model) {
        var books = bookService.findAll();
        model.addAttribute("books", books);
        return "book_list";
    }

    @GetMapping("/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "book_add";
    }

    @PostMapping("/add")
    public String saveNewBook(@ModelAttribute("book") Book book) {
        var genreIds = book.getGenres().stream()
                .map(genre -> genre.getId())
                .collect(Collectors.toSet());
        bookService.insert(book.getTitle(), book.getAuthor().getId(), genreIds);
        return "redirect:/book";
    }

    @GetMapping("/edit/{id}")
    public String editBook(@PathVariable Long id, Model model) {
        var book = bookService.findById(id);
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "book_edit";
    }

    @PostMapping("/edit")
    public String updateBook(@ModelAttribute("book") Book book) {
        var genreIds = book.getGenres().stream()
                .map(genre -> genre.getId())
                .collect(Collectors.toSet());
        bookService.update(book.getId(), book.getTitle(), book.getAuthor().getId(), genreIds);
        return "redirect:/book";
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
        return "redirect:/book";
    }
}
