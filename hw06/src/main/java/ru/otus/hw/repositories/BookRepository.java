package ru.otus.hw.repositories;

import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {

    List<Book> findAllWithGenres();

    Optional<Book> findById(long id);

    void deleteById(long id);

    Book save(Book book);
}
