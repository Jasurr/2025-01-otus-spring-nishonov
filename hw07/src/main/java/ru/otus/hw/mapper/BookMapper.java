package ru.otus.hw.mapper;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

public class BookMapper {

    public static BookDto toDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                new AuthorDto(book.getAuthor().getId(), book.getAuthor().getFullName()),
                book.getGenres().stream()
                        .map(genre -> new GenreDto(genre.getId(), genre.getName()))
                        .toList()
        );
    }

    public static Book toModel(BookDto dto) {
        return new Book(
                dto.getId(),
                dto.getTitle(),
                new Author(dto.getAuthor().getId(), dto.getAuthor().getFullName()),
                dto.getGenres()
                        .stream()
                        .map(genre -> new Genre(genre.getId(), genre.getName()))
                        .toList()
        );
    }
}
