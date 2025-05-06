package ru.otus.hw.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;

import java.io.IOException;
import java.util.List;

@ChangeUnit(id = "init-authors", order = "1", author = "user")
public class InitialDataMigration {

    @Execution
    public void execution(MongoTemplate mongoTemplate) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // Authors JSON faylni o'qish va MongoDB ga yuklash
        List<Author> authors = objectMapper.readValue(
                new ClassPathResource("data/authors.json").getInputStream(),
                new TypeReference<List<Author>>() {}
        );
        mongoTemplate.insert(authors, Author.class);

        // Books JSON faylni o'qish va MongoDB ga yuklash
        List<Book> books = objectMapper.readValue(
                new ClassPathResource("data/books.json").getInputStream(),
                new TypeReference<List<Book>>() {}
        );
        mongoTemplate.insert(books, Book.class);
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        mongoTemplate.dropCollection("books");
        mongoTemplate.dropCollection("authors");
    }
}
