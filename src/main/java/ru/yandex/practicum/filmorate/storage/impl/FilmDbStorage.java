package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.model.Film;
import ru.yandex.practicum.filmorate.controller.model.Pair;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
        String sql = "SELECT * from films as f " +
                     "LEFT JOIN mpa_rating AS m ON f.mpa = m.id_mpa ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film film = new Film(rs.getString("name"),
                    rs.getString("description"),
                    LocalDate.parse(rs.getString("releasedate")),
                    rs.getLong("duration"),
                    rs.getInt("rate"),
                    new Pair<>(rs.getInt("id_mpa"), rs.getString("rating")),
                    getGenresFilms(rs.getInt("id")));
            film.setId(rs.getLong("id"));
            return film;
        });
    }

    @Override
    public Film createFilm(Film film) {
        validateFilm(film);
        validateMpa(film);
        String sql = "INSERT into films VALUES(?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, currentId, film.getName(),
                film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getRate(), film.getMpa().id);
        film.setId(currentId);
        getUniqueID();
        if (film.getGenres() != null) {
            for (Pair<Integer, String> genre : film.getGenres()) {
                jdbcTemplate.update("insert into film_genre values(?, ?)", film.getId(), genre.id);
            }
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validateFilmUpdate(film);
        String sql = "UPDATE films SET name = ?, description = ?, releasedate = ?, " +
                " duration = ?, rate = ?, mpa = ? where id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getRate(), film.getMpa().id, film.getId());
        if (film.getGenres() != null) {
            String sql2 = "DELETE from film_genre where id_film = ?";
            jdbcTemplate.update(sql2, film.getId());
            for (Pair<Integer, String> genre : film.getGenres()) {
                jdbcTemplate.update("insert into film_genre values(?, ?)", film.getId(), genre.id);
            }
            List<Pair<Integer, String>> uniqueElements =
                    film.getGenres()
                            .stream()
                            .distinct()
                            .collect(Collectors.toList());
            film.setGenres(uniqueElements);
        }
        if (film.getGenres() == null) {
            String sql2 = "DELETE from film_genre where id_film = ?";
            jdbcTemplate.update(sql2, film.getId());
        }
        return getFilmById(film.getId());
    }

    @Override
    public Film getFilmById(long id) {
        if (id < 0) {
            throw new FilmNotFoundException("id фильма не может быть отрицательным числом");
        }
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select * from films AS f " +
                "LEFT JOIN mpa_rating AS m ON f.mpa = m.id_mpa " +
                "LEFT JOIN film_genre AS fg ON f.id = fg.id_film " +
                "LEFT JOIN  genre AS g ON fg.id_genre = g.id_genre " +
                "where id = ?", id);
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
                        new ArrayList<>()
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
        String sql = "INSERT INTO likes values(?, ?)";
        jdbcTemplate.update(sql, userId, filmId);
        String sql2 = "update films set rate = rate + 1 where id = ?";
        jdbcTemplate.update(sql2, filmId);
    }

    public void deleteLike(long filmId, long userId) {
        if (filmId < 0 || userId < 0) {
            log.error("id не может быть отрицательным числом");
            throw new UserNotFoundException("id не может быть отрицательным числом");
        }
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

    public List<Pair<Integer, String>> getGenresFilms(long id){
        String sql = "select * from films AS f " +
                "LEFT JOIN mpa_rating AS m ON f.mpa = m.id_mpa " +
                "LEFT JOIN film_genre AS fg ON f.id = fg.id_film " +
                "LEFT JOIN  genre AS g ON fg.id_genre = g.id_genre " +
                "where id = ?";
        List<Pair<Integer, String>> genre = jdbcTemplate.query(sql, (rs, rowNum) ->
                new Pair<>(rs.getInt("id_genre"), rs.getNString("genre")), id);
        List<Pair<Integer, String>> uniqueElements =
                genre
                        .stream()
                        .distinct()
                        .collect(Collectors.toList());
        return uniqueElements;
    }
}
