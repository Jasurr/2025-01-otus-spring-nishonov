package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.HashSet;
import java.util.Set;
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
        return "book/list";
    }

    @GetMapping("/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "book/add";
    }

    @PostMapping("/add")
    public String saveNewBook(@ModelAttribute("book") Book book) {
        var genreIds = book.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        bookService.insert(book.getTitle(), book.getAuthor().getId(), genreIds);
        return "redirect:/book";
    }

    @GetMapping("/edit/{id}")
    public String editBook(@PathVariable Long id, Model model) {
        var book = bookService.findById(id).get();
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "book/edit";
    }

    @PostMapping("/edit")
    public String updateBook(@ModelAttribute("book") Book book,
                             @RequestParam(value = "genreIds", required = false) Set<Long> genreIds) {
        if (genreIds == null) {
            genreIds = new HashSet<>();
        }
        bookService.update(book.getId(), book.getTitle(), book.getAuthor().getId(), genreIds);
        return "redirect:/book";
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
        return "redirect:/book";
    }
}
