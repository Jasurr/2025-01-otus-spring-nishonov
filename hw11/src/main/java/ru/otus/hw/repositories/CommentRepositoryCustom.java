package ru.otus.hw.repositories;

import reactor.core.publisher.Mono;
import ru.otus.hw.models.Book;

public interface CommentRepositoryCustom {
    Mono<Void> updateCommentsByBookId(String bookId, Book updatedBook);
}
