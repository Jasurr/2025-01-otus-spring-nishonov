package ru.otus.hw.migration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.dto.AuthorDTO;
import ru.otus.hw.dto.BookMigrateDTO;
import ru.otus.hw.dto.GenreDTO;
import ru.otus.hw.mapper.AuthorMapper;
import ru.otus.hw.mapper.GenreMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

@ChangeLog(order = "001")
public class InitialDataMigration {

    private static final Logger LOGGER = Logger.getLogger(InitialDataMigration.class.getName());

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ChangeSet(order = "001", id = "initAuthors", author = "Jasur")
    public void initAuthors(MongoTemplate mongoTemplate) throws IOException {
        // Load authors from JSON and save to MongoDB
        List<AuthorDTO> authorDTOs = readJson("data/authors.json", new TypeReference<>() {
        });
        List<Author> authors = authorDTOs.stream()
                .map(AuthorMapper::toDocument)
                .toList();
        authors.forEach(mongoTemplate::save);
        LOGGER.info("✅ Authors imported successfully: " + authors.size());
    }

    @ChangeSet(order = "002", id = "initGenres", author = "Jasur")
    public void initGenres(MongoTemplate mongoTemplate) throws IOException {
        // Load genres from JSON and save to MongoDB
        List<GenreDTO> genreDTOs = readJson("data/genres.json", new TypeReference<>() {
        });
        List<Genre> genres = genreDTOs.stream()
                .map(GenreMapper::toDocument)
                .toList();
        genres.forEach(mongoTemplate::save);
        LOGGER.info("✅ Genres imported successfully: " + genres.size());
    }

    @ChangeSet(order = "003", id = "initBooks", author = "Jasur")
    public void initBooks(MongoTemplate mongoTemplate) throws IOException {
        // Load books from JSON and save to MongoDB

        List<BookMigrateDTO> booksDTO = readJson("data/books.json", new TypeReference<>() {
        });
        var books = booksDTO.stream()
                .map(bookDTO -> {
                    Book book = new Book();
                    book.setTitle(bookDTO.getTitle());
                    book.setAuthorId(bookDTO.getAuthorId());
                    book.setGenreIds(bookDTO.getGenres());
                    return book;
                })
                .toList();

        books.forEach(mongoTemplate::save);
        LOGGER.info("✅ Books imported successfully: " + books.size());
    }

    /**
     * Reads a JSON file from the resources folder and converts it into a List of the given type.
     *
     * @param path          the path to the JSON file in resources
     * @param typeReference the type reference for deserialization
     * @param <T>           the type of objects to return
     * @return list of parsed objects
     * @throws IOException if the file is not found or cannot be read
     */
    private <T> List<T> readJson(String path, TypeReference<List<T>> typeReference) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            throw new IllegalStateException("❌ JSON file not found: " + path);
        }
        return objectMapper.readValue(inputStream, typeReference);
    }
}
