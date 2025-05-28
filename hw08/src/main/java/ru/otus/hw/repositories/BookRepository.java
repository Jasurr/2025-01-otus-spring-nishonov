package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends MongoRepository<Book, String> {

    @Aggregation(pipeline = {
            "{ $match: { _id: ?0 } }",
            "{ $lookup: { from: 'authors', localField: 'author.$id', foreignField: '_id', as: 'author' } }",
            "{ $unwind: { path: '$author', preserveNullAndEmptyArrays: true } }",
            "{ $lookup: { from: 'genres', localField: 'genres.$id', foreignField: '_id', as: 'genres' } }"
    })
    Optional<Book> findByIdWithRelations(String id);

    @Aggregation(pipeline = {
            "{ $lookup: { from: 'authors', localField: 'author.$id', foreignField: '_id', as: 'author' } }",
            "{ $unwind: { path: '$author', preserveNullAndEmptyArrays: false } }",
            "{ $lookup: { from: 'genres', localField: 'genres.$id', foreignField: '_id', as: 'genres' } }"
    })
    List<Book> findAllWithRelations();
}