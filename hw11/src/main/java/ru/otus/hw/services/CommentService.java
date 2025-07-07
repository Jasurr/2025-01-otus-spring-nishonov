package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;

public interface CommentService {

    Mono<CommentDto> findById(String id);

    Flux<CommentDto> findByBookId(String bookId);

    Mono<CommentDto> insert(String message, String bookId);

    Mono<CommentDto> update(String id, String message);

    Mono<Void> deleteById(String id);
}
