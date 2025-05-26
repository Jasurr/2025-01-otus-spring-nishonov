package ru.otus.hw.mapper;

import ru.otus.hw.dto.BookDTO;
import ru.otus.hw.models.Book;

public class BookMapper {

    public static BookDTO toDTO(Book book) {
        return new BookDTO(
                book.getId(),
                book.getTitle(),
                AuthorMapper.toDTO(book.getAuthor()),
                book.getGenres().stream()
                        .map(GenreMapper::toDTO)
                        .toList()
        );
    }
}
