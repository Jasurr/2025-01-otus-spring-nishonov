package ru.otus.hw.mapper;

import ru.otus.hw.dto.BookDTO;
import ru.otus.hw.models.Book;

public class BookMapper {

    public static BookDTO toDTO(Book book) {
        return new BookDTO(book.getId(),
                book.getTitle(),
                book.getAuthorId(),
                book.getGenreIds()
        );
    }

    public static Book toDocument(BookDTO bookDTO) {
        return new Book(
                bookDTO.getId(),
                bookDTO.getTitle(),
                bookDTO.getAuthor(),
                bookDTO.getGenres()
        );
    }
}
