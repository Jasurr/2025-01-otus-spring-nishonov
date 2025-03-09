package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {
    private final GenreRepository genreRepository;

    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public Optional<Book> findById(long id) {
        var sql = "select t.id, t.title, t.author_id, a.full_name from books t, authors a " +
                "where t.author_id = a.id and t.id = :id";
        var parameters = Map.of("id", id);
        var book = Optional.ofNullable(jdbcOperations.query(sql, parameters, new BookResultSetExtractor()));
        if (book.isPresent()) {
            var genreIds = getAllGenreRelationsByBookId(book.get().getId())
                    .stream()
                    .map(BookGenreRelation::genreId)
                    .collect(Collectors.toSet());
            var genres = genreRepository.findAllByIds(genreIds);
            book.get().setGenres(genres);
        }
        return book;
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        jdbcOperations.update("delete from books where id = :id", Map.of("id", id));
    }

    private List<Book> getAllBooksWithoutGenres() {
        var sql = "select t.id, t.title, t.author_id, a.full_name from books t, authors a where t.author_id = a.id";
        var books = jdbcOperations.query(sql, new BookRowMapper());
        return books.isEmpty() ? new ArrayList<>() : books;
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        var relations = jdbcOperations.query("select book_id, genre_id from books_genres",
                (rs, rowNum) -> new BookGenreRelation(rs.getLong("book_id"), rs.getLong("genre_id")));
        return relations.isEmpty() ? new ArrayList<>() : relations;
    }

    private List<BookGenreRelation> getAllGenreRelationsByBookId(long bookId) {
        var relations = jdbcOperations.query("select book_id, genre_id from books_genres where book_id = :book_id",
                Map.of("book_id", bookId),
                (rs, rowNum) -> new BookGenreRelation(rs.getLong("book_id"), rs.getLong("genre_id")));
        return relations.isEmpty() ? new ArrayList<>() : relations;
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        // Добавить книгам (booksWithoutGenres) жанры (genres) в соответствии со связями (relations)
        Map<Long, Genre> genreMap = genres.stream()
                .collect(Collectors.toMap(Genre::getId, Function.identity()));
        //
        booksWithoutGenres.forEach(book -> {
            relations.stream()
                    .filter(relation -> relation.bookId() == book.getId())
                    .map(relation -> genreMap.get(relation.genreId())) // O(1) da topiladi
                    .filter(Objects::nonNull)
                    .forEach(genre -> {
                        if (book.getGenres() == null) {
                            book.setGenres(new ArrayList<>());
                        }
                        book.getGenres().add(genre);
                    });
        });
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        var sql = "INSERT INTO books (title, author_id) VALUES (:title, :author_id)";

        var parameters = new MapSqlParameterSource()
                .addValue("title", book.getTitle())
                .addValue("author_id", book.getAuthor().getId());

        jdbcOperations.update(sql, parameters, keyHolder, new String[]{"id"});
        //noinspection DataFlowIssue

        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        int updateCount = jdbcOperations.update("update books set title = :title, author_id = :author_id where id = :id",
                Map.of("id", book.getId(), "title", book.getTitle(), "author_id", book.getAuthor().getId()));
        if (updateCount == 0) {
            throw new EntityNotFoundException("Book with id = " + book.getId() + " not found");
        }
        // Выбросить EntityNotFoundException если не обновлено ни одной записи в БД
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        // Использовать метод batchUpdate
        book.getGenres().forEach(genre -> {
            jdbcOperations.update("insert into books_genres (book_id, genre_id) values (:book_id, :genre_id)",
                    Map.of("book_id", book.getId(), "genre_id", genre.getId()));
        });
    }

    private void removeGenresRelationsFor(Book book) {
        jdbcOperations.update("delete from books_genres where book_id = :book_id", Map.of("book_id", book.getId()));
    }

    @RequiredArgsConstructor
    private static class BookRowMapper implements RowMapper<Book> {
        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            var book = new Book();
            book.setId(rs.getLong("id"));
            book.setTitle(rs.getString("title"));

            var author = new Author();
            author.setId(rs.getLong("author_id"));
            author.setFullName(rs.getString("full_name"));
            book.setAuthor(author);
            return book;
        }
    }

    // Использовать для findById
    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (!rs.next()) {
                return null;
            }
            var book = new Book();
            book.setId(rs.getLong("id"));
            book.setTitle(rs.getString("title"));
            var author = new Author();
            author.setId(rs.getLong("author_id"));
            author.setFullName(rs.getString("full_name"));
            book.setAuthor(author);
            return book;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
