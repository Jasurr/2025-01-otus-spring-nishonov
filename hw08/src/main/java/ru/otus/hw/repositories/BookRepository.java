package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.dto.BookDTO;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends MongoRepository<Book, String> {
    @Aggregation(pipeline = {
            "{ $lookup: { " +
                    "from: 'authors', " +
                    "let: { authorId: '$author_id' }, " +
                    "pipeline: [ " +
                    "{ $match: { $expr: { $eq: ['$_id', '$$authorId'] } } }, " +
                    "{ $project: { id: '$_id', fullName: '$full_name' } } " +
                    "], " +
                    "as: 'author' " +
                    "} }",
            "{ $lookup: { " +
                    "from: 'genres', " +
                    "let: { genreIds: '$genre_ids' }, " +
                    "pipeline: [ " +
                    "{ $match: { $expr: { $in: ['$_id', '$$genreIds'] } } }, " +
                    "{ $project: { id: '$_id', name: 1 } } " +
                    "], " +
                    "as: 'genres' " +
                    "} }",
            "{ $unwind: '$author' }",
            "{ $project: { id: '$_id', title: 1, author: 1, genres: 1 } }"
    })
    List<BookDTO> findAllWithAuthorAndGenres();

    @Aggregation(pipeline = {
            "{ $match: { _id: ?0 } }",
            "{ $lookup: { " +
                    "from: 'authors', " +
                    "let: { authorId: '$author_id' }, " +
                    "pipeline: [ " +
                    "{ $match: { $expr: { $eq: ['$_id', '$$authorId'] } } }, " +
                    "{ $project: { id: '$_id', fullName: '$full_name' } } " +  // fullName deb o'zgartirdik
                    "], " +
                    "as: 'author' " +
                    "} }",
            "{ $lookup: { " +
                    "from: 'genres', " +
                    "let: { genreIds: '$genre_ids' }, " +
                    "pipeline: [ " +
                    "{ $match: { $expr: { $in: ['$_id', '$$genreIds'] } } }, " +
                    "{ $project: { id: '$_id', name: 1 } } " +
                    "], " +
                    "as: 'genres' " +
                    "} }",
            "{ $unwind: '$author' }",
            "{ $project: { id: '$_id', title: 1, author: 1, genres: 1 } }"
    })
    Optional<BookDTO> findByIdWithAuthorAndGenres(String id);


}