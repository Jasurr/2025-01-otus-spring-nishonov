package ru.otus.hw.repositories;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.dto.BookDTO;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends MongoRepository<Book, String> {

    @Aggregation(pipeline = {
            "{ $lookup: { from: 'authors', localField: 'author_id', foreignField: '_id', as: 'author' } }",
            "{ $lookup: { from: 'genres', localField: 'genre_ids', foreignField: '_id', as: 'genres' } }",
            "{ $unwind: '$author' }",
            "{ $project: { _id: 1, title: 1, author: '$author', genres: '$genres' } }"
    })
    List<BookDTO> findAllWithAuthorAndGenres();

    @Aggregation(pipeline = {
            "{ $match: { _id: ?0 } }",
            "{ $lookup: { from: 'authors', localField: 'author_id', foreignField: '_id', as: 'author' } }",
            "{ $lookup: { from: 'genres', localField: 'genre_ids', foreignField: '_id', as: 'genres' } }",
            "{ $unwind: '$author' }",
            "{ $project: { _id: 1, title: 1, author: '$author', genres: '$genres' } }"
    })
    Optional<BookDTO> findByIdWithAuthorAndGenres(String id);
}