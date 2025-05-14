package ru.otus.hw.migration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.dto.AuthorDTO;
import ru.otus.hw.dto.BookDTO;
import ru.otus.hw.dto.GenreDTO;
import ru.otus.hw.mapper.AuthorMapper;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.mapper.GenreMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

@ChangeUnit(id = "init-data", order = "001", author = "Jasur")
@RequiredArgsConstructor
public class InitialDataMigration {

    private static final Logger LOGGER = Logger.getLogger(InitialDataMigration.class.getName());

    private final MongoTemplate mongoTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Execution
    public void changeSet() throws IOException {
        loadAndInsert("data/authors.json", new TypeReference<>() {}, AuthorDTO.class, AuthorMapper::toDocument, Author.class);
        loadAndInsert("data/genres.json", new TypeReference<>() {}, GenreDTO.class, GenreMapper::toDocument, Genre.class);
        loadAndInsert("data/books.json", new TypeReference<>() {}, BookDTO.class, BookMapper::toDocument, Book.class);

        LOGGER.info("✅ Data migration executed successfully");
    }

    private <D, T> void loadAndInsert(String resourcePath,
                                      TypeReference<List<D>> typeRef,
                                      Class<D> dtoClass,
                                      Function<D, T> mapper,
                                      Class<T> entityClass) throws IOException {
        try (InputStream input = new ClassPathResource(resourcePath).getInputStream()) {
            List<D> dtoList = objectMapper.readValue(input, typeRef);
            List<T> documents = dtoList.stream().map(mapper).toList();
            for (T doc : documents) {
                insertIfNotExists(doc, entityClass);
            }
        } catch (IOException e) {
            LOGGER.severe("❌ Failed to read file " + resourcePath + ": " + e.getMessage());
            throw e;
        }
    }

    private <T> void insertIfNotExists(T document, Class<T> entityClass) {
        var id = mongoTemplate.getConverter().convertToMongoType(
                mongoTemplate.getConverter().getMappingContext().getPersistentEntity(entityClass).getIdentifierAccessor(document).getIdentifier());

        if (mongoTemplate.findById(id, entityClass) == null) {
            mongoTemplate.insert(document);
        } else {
            LOGGER.warning(entityClass.getSimpleName() + " with id " + id + " already exists.");
        }
    }

    @RollbackExecution
    public void rollback() {
        // optional: rollback qilinadigan ishlar
    }
}
