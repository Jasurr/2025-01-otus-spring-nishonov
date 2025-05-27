package ru.otus.hw.mapper;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Book;

public class BookMapper {

    public static BookDto toDTO(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                new AuthorDto(
                        book.getAuthor().getId(),
                        book.getAuthor().getFullName()
                ),
                book.getGenres().stream()
                        .map(GenreMapper::toDTO)
                        .toList()
        );
    }
}
