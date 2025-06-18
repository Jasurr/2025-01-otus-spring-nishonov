package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;


@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {
    private final ReactiveMongoOperations reactiveMongoOperations;

    @Override
    public Mono<Void> updateCommentsByBookId(String bookId, Book updatedBook) {
        Query query = new Query(Criteria.where("book.id").is(bookId));
        Update update = new Update().set("book", updatedBook);
        return reactiveMongoOperations.updateMulti(query, update, Comment.class)
                .then();
    }
}
