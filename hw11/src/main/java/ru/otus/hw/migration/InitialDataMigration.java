package ru.otus.hw.migration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentRequest;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitialDataMigration {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitialDataMigration.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Bean
    public CommandLineRunner initData() {
        return args -> init()
                .doOnSuccess(v -> LOGGER.info("✅ Data migration completed successfully"))
                .doOnError(e -> LOGGER.error("❌ Data migration failed", e))
                .block(); // Block only in CommandLineRunner for initialization
    }

    public Mono<Void> init() {
        return Mono.when(
                initAuthors(),
                initGenres(),
                initBooks(),
                initComments()
        );
    }

    public Mono<Void> initAuthors() {
        return readJson("data/authors.json", new TypeReference<List<AuthorDto>>() {})
                .flatMapMany(authorDTOs -> Flux.fromIterable(authorDTOs)
                        .map(dto -> new Author(dto.id(), dto.fullName()))
                        .flatMap(author -> reactiveMongoTemplate.save(author)))
                .doOnComplete(() -> LOGGER.info("✅ Authors imported successfully"))
                .then();
    }

    public Mono<Void> initGenres() {
        return readJson("data/genres.json", new TypeReference<List<GenreDto>>() {})
                .flatMapMany(genreDTOs -> Flux.fromIterable(genreDTOs)
                        .map(dto -> new Genre(dto.id(), dto.name()))
                        .flatMap(genre -> reactiveMongoTemplate.save(genre)))
                .doOnComplete(() -> LOGGER.info("✅ Genres imported successfully"))
                .then();
    }

    public Mono<Void> initBooks() {
        return readJson("data/books.json", new TypeReference<List<BookDto>>() {})
                .flatMapMany(bookDTOs -> Flux.fromIterable(bookDTOs)
                        .map(dto -> {
                            Book book = new Book();
                            book.setId(dto.id());
                            book.setTitle(dto.title());
                            book.setAuthor(new Author(dto.author().id(), dto.author().fullName()));
                            book.setGenres(dto.genres().stream()
                                    .map(genreDto -> new Genre(genreDto.id(), genreDto.name()))
                                    .toList());
                            return book;
                        })
                        .flatMap(book -> reactiveMongoTemplate.save(book)))
                .doOnComplete(() -> LOGGER.info("✅ Books imported successfully"))
                .then();
    }

    public Mono<Void> initComments() {
        return readJson("data/comments.json", new TypeReference<List<CommentRequest>>() {})
                .flatMapMany(commentDTOs -> Flux.fromIterable(commentDTOs)
                        .map(dto -> {
                            Comment comment = new Comment();
                            Book book = new Book();
                            book.setId(dto.bookId());
                            comment.setBook(book);
                            comment.setMessage(dto.message());
                            return comment;
                        })
                        .flatMap(comment -> reactiveMongoTemplate.save(comment)))
                .doOnComplete(() -> LOGGER.info("✅ Comments imported successfully"))
                .then();
    }

    /**
     * Reads a JSON file from the resources folder and converts it into a Mono of List of the given type.
     *
     * @param path          the path to the JSON file in resources
     * @param typeReference the type reference for deserialization
     * @param <T>           the type of objects to return
     * @return Mono emitting a list of parsed objects
     */
    private <T> Mono<List<T>> readJson(String path, TypeReference<List<T>> typeReference) {
        return Mono.fromCallable(() -> {
            try (var inputStream = new ClassPathResource(path).getInputStream()) {
                return objectMapper.readValue(inputStream, typeReference);
            } catch (IOException e) {
                throw new IllegalStateException("❌ JSON file not found or cannot be read: " + path, e);
            }
        }).onErrorMap(e -> new IllegalStateException("❌ Failed to read JSON: " + path, e));
    }
}