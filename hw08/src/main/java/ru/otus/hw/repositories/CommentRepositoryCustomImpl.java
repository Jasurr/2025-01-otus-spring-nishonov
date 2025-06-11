package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;


@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {
    private final MongoOperations mongoOperations;

    public void updateCommentsByBookId(String bookId, Book updatedBook) {
        Query query = new Query(Criteria.where("book.id").is(bookId));
        Update update = new Update().set("book", updatedBook);
        mongoOperations.updateMulti(query, update, Comment.class);
    }
}
