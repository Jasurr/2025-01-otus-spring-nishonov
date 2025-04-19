package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(value = "Book.withGenresAndAuthor", type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT b FROM Book b")
    List<Book> findAllWithGenres();

    @EntityGraph(value = "Book.withGenresAndAuthor", type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT b FROM Book b WHERE b.id = :id")
    Optional<Book> findById(@Param("id") long id);
}
