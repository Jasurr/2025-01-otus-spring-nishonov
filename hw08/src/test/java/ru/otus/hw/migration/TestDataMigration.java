package ru.otus.hw.migration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mapper.AuthorMapper;
import ru.otus.hw.mapper.GenreMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.Book;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@ChangeLog(order = "test")
public class TestDataMigration {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ChangeSet(order = "001", id = "testAuthors", author = "test")
    public void initTestAuthors(MongoTemplate mongoTemplate) throws IOException {
        List<AuthorDto> authorDTOs = readJson("data/test-authors.json", new TypeReference<>() {
        });
        List<Author> authors = authorDTOs.stream().map(author -> new Author(author.id(), author.fullName())).toList();
        authors.forEach(mongoTemplate::save);
    }

    @ChangeSet(order = "002", id = "testGenres", author = "test")
    public void initTestGenres(MongoTemplate mongoTemplate) throws IOException {
        List<GenreDto> genreDTOs = readJson("data/test-genres.json", new TypeReference<>() {
        });
        List<Genre> genres = genreDTOs.stream().map(genre -> new Genre(genre.id(), genre.name())).toList();
        genres.forEach(mongoTemplate::save);
    }

    @ChangeSet(order = "003", id = "testBooks", author = "test")
    public void initTestBooks(MongoTemplate mongoTemplate) throws IOException {
        List<BookDto> booksDTO = readJson("data/test-books.json", new TypeReference<>() {
        });
        List<Book> books = booksDTO.stream().map(dto -> {
            Book book = new Book();
            book.setTitle(dto.title());
            book.setAuthor(new Author(dto.author().id(), dto.author().fullName()));
            book.setGenres(dto.genres().stream()
                    .map(genreDto -> new Genre(genreDto.id(), genreDto.name()))
                    .toList());
            return book;
        }).toList();
        books.forEach(mongoTemplate::save);
    }

    private <T> List<T> readJson(String path, TypeReference<List<T>> typeReference) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            throw new IllegalStateException("Test JSON file not found: " + path);
        }
        return objectMapper.readValue(inputStream, typeReference);
    }
}
