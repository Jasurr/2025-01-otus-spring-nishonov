package ru.otus.hw.migration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.data.mongodb.clear-on-startup:false}")
    private boolean clearOnStartup;

    @PostConstruct
    public void initData() {
        (clearOnStartup ? clearAndMigrate() : init())
                .doOnSuccess(v -> LOGGER.info("✅ Data migration completed successfully"))
                .doOnError(e -> LOGGER.error("❌ Data migration failed", e))
                .block();
    }

    public Mono<Void> clearAndMigrate() {
        return clearCollections().then(init());
    }

    private Mono<Void> clearCollections() {
        return reactiveMongoTemplate.dropCollection(Comment.class)
                .then(reactiveMongoTemplate.dropCollection(Book.class))
                .then(reactiveMongoTemplate.dropCollection(Genre.class))
                .then(reactiveMongoTemplate.dropCollection(Author.class))
                .doOnSuccess(v -> LOGGER.info("Clear collections completed successfully"))
                .doOnError(e -> LOGGER.error("clear collections failed", e))
                .then();

    }

    private Mono<Void> init() {
        return initAuthors()
                .then(initGenres())
                .then(initBooks())
                .then(initComments());
    }

    public Mono<Void> initAuthors() {
        return readJson("data/authors.json", new TypeReference<List<AuthorDto>>() {
        })
                .flatMapMany(authorDTOs -> Flux.fromIterable(authorDTOs)
                        .map(dto -> new Author(dto.id(), dto.fullName()))
                        .flatMap(author -> reactiveMongoTemplate.save(author)))
                .doOnComplete(() -> LOGGER.info("✅ Authors imported successfully"))
                .then();
    }

    public Mono<Void> initGenres() {
        return readJson("data/genres.json", new TypeReference<List<GenreDto>>() {
        })
                .flatMapMany(genreDTOs -> Flux.fromIterable(genreDTOs)
                        .map(dto -> new Genre(dto.id(), dto.name()))
                        .flatMap(genre -> reactiveMongoTemplate.save(genre)))
                .doOnComplete(() -> LOGGER.info("✅ Genres imported successfully"))
                .then();
    }

    public Mono<Void> initBooks() {
        return readJson("data/books.json", new TypeReference<List<BookDto>>() {
        })
                .flatMapMany(bookDTOs -> Flux.fromIterable(bookDTOs)
                        .flatMap(dto -> {
                            if (dto.author() == null || dto.author().id() == null) {
                                return Mono.empty();
                            } // Skip invalid books
                            return reactiveMongoTemplate.findById(dto.author().id(), Author.class)
                                    .switchIfEmpty(Mono.error(
                                            new IllegalStateException("Author not found: " + dto.author().id())))
                                    .flatMap(author -> Flux.fromIterable(dto.genres())
                                            .flatMap(gDto -> reactiveMongoTemplate.findById(gDto.id(), Genre.class)
                                                    .switchIfEmpty(Mono.error(
                                                            new IllegalStateException("Genre not found: " + gDto.id())))
                                            )
                                            .collectList()
                                            .map(genres -> new Book(dto.id(), dto.title(), author, genres))
                                    );
                        })
                        .flatMap(book -> reactiveMongoTemplate.save(book)))
                .doOnComplete(() -> LOGGER.info("✅ Books imported successfully"))
                .then();
    }

    public Mono<Void> initComments() {
        return readJson("data/comments.json", new TypeReference<List<CommentRequest>>() {
        })
                .flatMapMany(commentDTOs -> Flux.fromIterable(commentDTOs)
                        .flatMap(dto -> reactiveMongoTemplate.findById(dto.bookId(), Book.class)
                                .switchIfEmpty(Mono.error(new IllegalStateException("Book not found: " + dto.bookId())))
                                .map(book -> {
                                    Comment comment = new Comment();
                                    comment.setBook(book);
                                    comment.setMessage(dto.message());
                                    return comment;
                                }))
                        .flatMap(comment -> reactiveMongoTemplate.save(comment)))
                .doOnComplete(() -> LOGGER.info("✅ Comments imported successfully"))
                .then();
    }

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