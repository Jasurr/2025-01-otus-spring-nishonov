package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.rest.dto.BookDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    @Override
    public Optional<BookDto> findById(long id) {
        var book = bookRepository.findById(id).get();
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setGenres(book.getGenres());
        return Optional.ofNullable(dto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(book -> {
                    BookDto dto = new BookDto();
                    dto.setId(book.getId());
                    dto.setTitle(book.getTitle());
                    dto.setAuthor(book.getAuthor());
                    dto.setGenres(book.getGenres());
                    return dto;
                })
                .toList();
    }

    @Transactional
    @Override
    public BookDto insert(String title, long authorId, Set<Long> genresIds) {
        return save(null, title, authorId, genresIds);
    }

    @Transactional
    @Override
    public BookDto update(long id, String title, long authorId, Set<Long> genresIds) {
        if (id <= 0) {
            throw new IllegalArgumentException("Book ID must be greater than 0");
        }
        return save(id, title, authorId, genresIds);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }

    private BookDto save(Long id, String title, long authorId, Set<Long> genresIds) {
        validateInputs(title, genresIds);
        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        var genres = genreRepository.findAllByIdIn(genresIds);
        if (genres.size() != genresIds.size()) {
            throw new EntityNotFoundException("One or more genres with ids %s not found".formatted(genresIds));
        }

        Book book;
        if (id == null) {
            book = new Book();
        } else {
            book = bookRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(id)));
        }

        book.setTitle(title);
        book.setAuthor(author);
        book.setGenres(genres);
        bookRepository.save(book);
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setGenres(book.getGenres());
        return dto;
    }

    private void validateInputs(String title, Set<Long> genresIds) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title must not be null or empty");
        }
        if (genresIds == null || genresIds.isEmpty()) {
            throw new IllegalArgumentException("Genres ids must not be null or empty");
        }
    }
}
