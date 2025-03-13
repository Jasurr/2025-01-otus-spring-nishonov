package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {
    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public List<Genre> findAll() {
        var genres = jdbcOperations.query("select id, name from genres", new GnreRowMapper());
        return genres.isEmpty() ? new ArrayList<>() : genres;

    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        var genres = jdbcOperations.query("select id, name from genres where id in (:ids)",
                Map.of("ids", ids), new GnreRowMapper());
        return genres.isEmpty() ? new ArrayList<>() : genres;
    }

    private static class GnreRowMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int i) throws SQLException {
            var genre = new Genre();
            genre.setId(rs.getLong("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }
    }
}
