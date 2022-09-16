package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.model.Film;
import ru.yandex.practicum.filmorate.controller.model.Pair;import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validation.ValidateFilm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.validation.ValidateFilm.validateFilm;
import static ru.yandex.practicum.filmorate.validation.ValidateFilm.validateFilmUpdate;
import static ru.yandex.practicum.filmorate.validation.ValidateRatingMpa.validateMpa;

@Component
@Slf4j
@Qualifier
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private int currentId = 1;

    private void getUniqueID() {
        currentId++;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * from films f " +
                "LEFT JOIN mpa_rating m ON f.mpa = m.id_mpa";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film createFilm(Film film) {
        validateFilm(film);
        validateMpa(film);
        if(jdbcTemplate.query("SELECT id from films",
                (rs, rowNum) -> rs.getLong("id")).contains(film.getId())){
            log.error("фильм с таким номером уже существует");
            throw new ValidationException("фильм с таким номером уже существует");
        }
        String sql = "INSERT into films VALUES(?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, currentId, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getRate(), film.getMpa().id);
        film.setId(currentId);
        getUniqueID();
        if (film.getGenres() != null) {
            for (Pair<Integer, String> genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genre VALUES (?, ?)", film.getId(), genre.id);
            }
        }
        film.setMpa(getMpaFilms(film.getId()));
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validateFilmUpdate(film);
        String sql = "UPDATE films SET name = ?, description = ?, releasedate = ?, duration = ?, rate = ?, mpa = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getRate(), film.getMpa().id, film.getId());
        if (film.getGenres() != null) {
            String sql2 = "DELETE FROM film_genre " +
                    "WHERE id_film = ?";
            jdbcTemplate.update(sql2, film.getId());
            for (Pair<Integer, String> genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genre VALUES(?, ?)", film.getId(), genre.id);
            }
        }
        if (film.getGenres() == null) {
            String sql2 = "DELETE FROM film_genre WHERE id_film = ?";
            jdbcTemplate.update(sql2, film.getId());
        }
        return getFilmById(film.getId());
    }

    @Override
    public Film getFilmById(long id) {
        ValidateFilm.validateIdFilm(id);
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select * from films f " +
                "LEFT JOIN mpa_rating m ON f.mpa = m.id_mpa " +
                "LEFT JOIN film_genre fg ON f.id = fg.id_film " +
                "LEFT JOIN  genre g ON fg.id_genre = g.id_genre " +
                "WHERE id = ?", id);
        if (rowSet.next()) {
            Pair mpa = new Pair<>(rowSet.getInt("id_mpa"), rowSet.getString("rating"));
            Pair genre1 = new Pair(rowSet.getInt("id_genre"), rowSet.getString("genre"));
            if (genre1.name == null || genre1.id == null) {
                Film film = new Film(
                        rowSet.getString("name"),
                        rowSet.getString("description"),
                        LocalDate.parse(rowSet.getString("releasedate")),
                        rowSet.getLong("duration"),
                        rowSet.getInt("rate"),
                        mpa,
                        new HashSet<>()
                );
                film.setId(id);
                return film;
            }
            Film film = new Film(
                    rowSet.getString("name"),
                    rowSet.getString("description"),
                    LocalDate.parse(rowSet.getString("releasedate")),
                    rowSet.getLong("duration"),
                    rowSet.getInt("rate"),
                    mpa,
                    getGenresFilms(id)
            );
            film.setId(id);
            return film;
        }
        return null;
    }

    public void addLike(long filmId, long userId) {
        ValidateFilm.validateIdFilm(filmId);
        ValidateFilm.validateIdFilm(userId);
        String sql = "INSERT INTO likes values(?, ?)";
        jdbcTemplate.update(sql, userId, filmId);
        String sql2 = "update films set rate = rate + 1 where id = ?";
        jdbcTemplate.update(sql2, filmId);
    }

    public void deleteLike(long filmId, long userId) {
        ValidateFilm.validateIdFilm(filmId);
        ValidateFilm.validateIdFilm(userId);
        String sql = "DELETE from likes where user_id = ? and film_id = ?";
        jdbcTemplate.update(sql, userId, filmId);
        String sql2 = "update films set rate = rate - 1 where id = ?";
        jdbcTemplate.update(sql2, filmId);
    }

    public List<Film> getTopFilms(int count) {
        String sql = "SELECT *,  from films AS f " +
                "LEFT JOIN mpa_rating as mpa on f.mpa = mpa.id_mpa " +
                "ORDER BY rate DESC LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Pair pair = new Pair<>(rs.getInt("mpa"), rs.getString("rating"));
            Film film = new Film(rs.getString("name"), rs.getString("description"),
                    LocalDate.parse(rs.getString("releasedate")), rs.getLong("duration"),
                    rs.getInt("rate"), pair, getGenresFilms(rs.getLong("id")));
            film.setId(rs.getLong("id"));
            return getFilmById(film.getId());
        }, count);
    }

    public Set<Pair<Integer, String>> getGenresFilms(long id) {
        String sql = "select * from films AS f " +
                "LEFT JOIN mpa_rating AS m ON f.mpa = m.id_mpa " +
                "LEFT JOIN film_genre AS fg ON f.id = fg.id_film " +
                "LEFT JOIN  genre AS g ON fg.id_genre = g.id_genre " +
                "where id = ?";
        List<Pair<Integer, String>> genres = jdbcTemplate.query(sql, (rs, rowNum) -> {
            if (rs.getInt("id_genre") == 0 || rs.getString("genre") == null) {
                return null;
            }
            return new Pair<>(rs.getInt("id_genre"), rs.getNString("genre"));
        }, id);
        return genres.stream().sorted(Comparator.comparing(o -> o.id)).
                collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Pair<Integer, String> getMpaFilms(long id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select * from films f " +
                "LEFT JOIN mpa_rating m ON f.mpa = m.id_mpa " +
                "LEFT JOIN film_genre fg ON f.id = fg.id_film " +
                "LEFT JOIN  genre g ON fg.id_genre = g.id_genre " +
                "WHERE id = ?", id);
        if (rowSet.next()) {
            return new Pair<>(rowSet.getInt("id_mpa"), rowSet.getString("rating"));
        }
        return null;
    }

    public Film makeFilm(ResultSet rs) throws SQLException {
        Film film = new Film(rs.getString("name"),
                rs.getString("description"),
                LocalDate.parse(rs.getString("releasedate")),
                rs.getLong("duration"),
                rs.getInt("rate"),
                new Pair<>(rs.getInt("id_mpa"), rs.getString("rating")),
                getGenresFilms(rs.getInt("id")));
        film.setId(rs.getLong("id"));
        return film;
    }
}
