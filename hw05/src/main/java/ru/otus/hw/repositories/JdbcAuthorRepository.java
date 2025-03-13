package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcAuthorRepository implements AuthorRepository {
    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public List<Author> findAll() {
        return jdbcOperations.query("select id, full_name from authors", new AuthorRowMapper());
    }

    @Override
    public Optional<Author> findById(long id) {
        return jdbcOperations.query("select id, full_name from authors where id = :id",
                        Map.of("id", id), new AuthorRowMapper())
                .stream()
                .findFirst();
    }

    private static class AuthorRowMapper implements RowMapper<Author> {
        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            var author = new Author();
            author.setId(rs.getLong("id"));
            author.setFullName(rs.getString("full_name"));
            return author;
        }
    }
}
