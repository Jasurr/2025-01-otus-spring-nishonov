package ru.otus.hw.migration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.dto.AuthorDTO;
import ru.otus.hw.dto.GenreDTO;
import ru.otus.hw.dto.BookMigrateDTO;
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
        List<AuthorDTO> authorDTOs = readJson("data/test-authors.json", new TypeReference<>() {
        });
        List<Author> authors = authorDTOs.stream().map(AuthorMapper::toDocument).toList();
        authors.forEach(mongoTemplate::save);
    }

    @ChangeSet(order = "002", id = "testGenres", author = "test")
    public void initTestGenres(MongoTemplate mongoTemplate) throws IOException {
        List<GenreDTO> genreDTOs = readJson("data/test-genres.json", new TypeReference<>() {
        });
        List<Genre> genres = genreDTOs.stream().map(GenreMapper::toDocument).toList();
        genres.forEach(mongoTemplate::save);
    }

    @ChangeSet(order = "003", id = "testBooks", author = "test")
    public void initTestBooks(MongoTemplate mongoTemplate) throws IOException {
        List<BookMigrateDTO> booksDTO = readJson("data/test-books.json", new TypeReference<>() {
        });
        List<Book> books = booksDTO.stream().map(dto -> {
            Book book = new Book();
            book.setTitle(dto.getTitle());
            book.setAuthorId(dto.getAuthorId());
            book.setGenreIds(dto.getGenres());
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
