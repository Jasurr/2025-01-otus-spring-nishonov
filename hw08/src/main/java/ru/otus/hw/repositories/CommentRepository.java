package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import ru.otus.hw.models.Comment;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {

    @Query(value = "{ 'book.$id': { '$oid': ?0 } }", fields = "{ '_id': 1, 'message': 1 }")
    List<Comment> findByBookId(String bookId);
}