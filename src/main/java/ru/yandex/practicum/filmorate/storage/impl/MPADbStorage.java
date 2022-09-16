package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.model.Pair;
import ru.yandex.practicum.filmorate.exeption.MpaNotFoundException;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.util.List;

@Component
@Slf4j
@Qualifier
@RequiredArgsConstructor
public class MPADbStorage implements MPAStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Pair<Integer, String>> getAllMPA() {
        String sql = "SELECT * from mpa_rating";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Pair<>(rs.getInt("id_mpa"), rs.getString("rating")));
    }

    @Override
    public Pair<Integer, String> getMpaById(int mpaId) {
        if (mpaId < 0 || mpaId > 5) {
            throw new MpaNotFoundException("id не может быть отрицательным числом");
        }
        String sql = "SELECT * from mpa_rating WHERE id_mpa = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, mpaId);
        if (sqlRowSet.next()) {
            Pair pair = new Pair<>(sqlRowSet.getInt("id_mpa"), sqlRowSet.getString("rating"));
            return pair;
        }
        return null;
    }
}
