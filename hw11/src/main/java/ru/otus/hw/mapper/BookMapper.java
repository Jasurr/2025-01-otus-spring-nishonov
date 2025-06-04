package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

@Component
public class BookMapper {

    public BookDto toDto(Book book) {
        if (book == null) {
            return null;
        }
        return new BookDto(
                book.getId(),
                book.getTitle(),
                new AuthorDto(
                        book.getAuthor().getId(),
                        book.getAuthor().getFullName()
                ),
                book.getGenres().stream()
                        .map(genre -> new GenreDto(
                                genre.getId(),
                                genre.getName()
                        ))
                        .toList()
        );
    }

    public Book toEntity(BookDto bookDto) {
        if (bookDto == null) {
            return null;
        }
        return new Book(
                bookDto.id(),
                bookDto.title(),
                new Author(
                        bookDto.author().id(),
                        bookDto.author().fullName()
                ),
                bookDto.genres().stream()
                        .map(genre -> new Genre(
                                genre.id(),
                                genre.name()
                        ))
                        .toList()
        );
    }
}
