package ru.otus.hw.mapper;

import ru.otus.hw.dto.AuthorDTO;
import ru.otus.hw.dto.BookDTO;
import ru.otus.hw.dto.GenreDTO;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.stream.Collectors;

public class BookMapper {

    public static BookDTO toDTO(Book book, AuthorDTO author, List<GenreDTO> genres) {
        return new BookDTO(
                book.getId(),
                book.getTitle(),
                author,
                genres
        );
    }

    public static Book toDocument(BookDTO bookDTO) {
        return new Book(
                bookDTO.getId(),
                bookDTO.getTitle(),
                bookDTO.getAuthor() != null ? bookDTO.getAuthor().getId() : null,
                bookDTO.getGenres() != null
                        ? bookDTO.getGenres().stream()
                        .map(GenreDTO::getId)
                        .collect(Collectors.toList())
                        : null
        );
    }
}
