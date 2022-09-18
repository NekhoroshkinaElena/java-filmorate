package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.model.Pair;
import ru.yandex.practicum.filmorate.exeption.GenreNotFoundException;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Pair<Integer, String>> getAllGenres() {
        String sql = "SELECT * FROM genre";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Pair<>(rs.getInt("id_genre"),
                rs.getString("genre")));
    }

    @Override
    public Pair<Integer, String> getGenreById(int genreId) {
        if (genreId < 0 || genreId > 6) {
            log.error("жанр с таким id не сущствует");
            throw new GenreNotFoundException("жанр с таким id не сущствует");
        }
        String sql = "SELECT * FROM genre WHERE id_genre = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, genreId);
        if (sqlRowSet.next()) {
            return new Pair<>(sqlRowSet.getInt("id_genre"), sqlRowSet.getString("genre"));
        }
        return null;
    }
}
