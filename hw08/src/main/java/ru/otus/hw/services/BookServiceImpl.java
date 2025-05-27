package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

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
    public Optional<BookDto> findById(String id) {
        return null;

//        return bookRepository.findByIdWithAuthorAndGenres(id)
//                .map(BookMapper::toDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(BookMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public BookDto insert(String title, String authorId, Set<String> genresIds) {
        return save(null, title, authorId, genresIds);
    }

    @Transactional
    @Override
    public BookDto update(String id, String title, String authorId, Set<String> genresIds) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Book ID must be greater than 0");
        }
        return save(id, title, authorId, genresIds);
    }

    @Transactional
    @Override
    public void deleteById(String id) {
        bookRepository.deleteById(id);
    }

    public BookDto save(String id, String title, String authorId, Set<String> genresIds) {
        validateInputs(id, title, authorId, genresIds);

        // Muallifni tekshirish
        if (!authorRepository.existsById(authorId)) {
            throw new EntityNotFoundException("Author with id %s not found".formatted(authorId));
        }

        // Janrlarni tekshirish
        long existingGenresCount = genreRepository.countByIdIn(genresIds);
        if (existingGenresCount != genresIds.size()) {
            throw new EntityNotFoundException("One or more genres with ids %s not found".formatted(genresIds));
        }

        // Kitobni yaratish yoki yangilash
        Book book = id == null ? new Book() : bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(id)));

        book.setTitle(title);

//        book.setAuthorId(authorId);
//        book.setGenreIds(List.copyOf(genresIds)); // Set ni List ga aylantirish
        book.setAuthor(authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(authorId))));

        bookRepository.save(book);

        return BookMapper.toDTO(book);
    }

    private void validateInputs(String id, String title, String authorId, Set<String> genresIds) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title must not be null or empty");
        }
        if (authorId == null || authorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Author ID must not be null or empty");
        }
        if (genresIds == null || genresIds.isEmpty()) {
            throw new IllegalArgumentException("Genres IDs must not be null, empty, or contain null elements");
        }
        if (id != null && id.trim().isEmpty()) {
            throw new IllegalArgumentException("Book ID must not be empty if provided");
        }
    }
}
