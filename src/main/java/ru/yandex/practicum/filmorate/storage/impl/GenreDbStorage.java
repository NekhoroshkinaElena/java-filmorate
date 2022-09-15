package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.model.Pair;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundException;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Component
@Slf4j
@Qualifier
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Pair> getAllGenres() {
        String sql = "SELECT * from genre";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Pair<>(rs.getInt("id_genre"),
                rs.getString("genre")));
    }

    @Override
    public Pair<Integer, String> getGenreById(int genreId) {
        if (genreId < 0) {
            throw new FilmNotFoundException("id не может быть отрицательным числом");//создать новое исключение
        }
        String sql = "SELECT * from genre WHERE id_genre = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, genreId);
        if(sqlRowSet.next()){
            Pair pair = new Pair<>(sqlRowSet.getInt("id_genre"), sqlRowSet.getString("genre"));
            return pair;
        }
        return null;
    }
}
