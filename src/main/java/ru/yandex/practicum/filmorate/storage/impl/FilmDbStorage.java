package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.model.Film;
import ru.yandex.practicum.filmorate.controller.model.Pair;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validation.ValidateFilm;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.validation.ValidateFilm.validateFilm;
import static ru.yandex.practicum.filmorate.validation.ValidateFilm.validateFilmUpdate;
import static ru.yandex.practicum.filmorate.validation.ValidateRatingMpa.validateMpa;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private int currentId = 1;

    private int getUniqueID() {
        return currentId++;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "select * from films f " +
                "LEFT JOIN mpa_rating m ON f.mpa = m.id_mpa " +
                "LEFT JOIN film_genre fg ON f.id = fg.id_film " +
                "LEFT JOIN  genre g ON fg.id_genre = g.id_genre ";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        return makeFilms(rowSet);
    }

    @Override
    public Film createFilm(Film film) {
        validateFilm(film);
        validateMpa(film);
        if (jdbcTemplate.query("SELECT id FROM films",
                (rs, rowNum) -> rs.getLong("id")).contains(film.getId())) {
            log.error("фильм с таким номером уже существует");
            throw new ValidationException("фильм с таким номером уже существует");
        }
        String sql = "INSERT INTO films VALUES(?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, currentId, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getRate(), film.getMpa().getId());
        film.setId(getUniqueID());
        if (film.getGenres() != null) {
            for (Pair<Integer, String> genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genre VALUES (?, ?)", film.getId(), genre.getId());
            }
        }
        film.setMpa(getMpaFilms(film.getId()));
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validateFilmUpdate(film);
        String sql = "UPDATE films SET name = ?," +
                " description = ?," +
                " releasedate = ?," +
                " duration = ?," +
                " rate = ?," +
                " mpa = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getRate(), film.getMpa().getId(), film.getId());
        if (film.getGenres() != null) {
            String sql2 = "DELETE FROM film_genre " +
                    "WHERE id_film = ?";
            jdbcTemplate.update(sql2, film.getId());
            for (Pair<Integer, String> genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genre VALUES(?, ?)", film.getId(), genre.getId());
            }
        } else {
            String sql2 = "DELETE FROM film_genre WHERE id_film = ?";
            jdbcTemplate.update(sql2, film.getId());
        }
        return getFilmById(film.getId());
    }

    @Override
    public Film getFilmById(long id) {
        ValidateFilm.validateIdFilm(id);
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT * FROM films f " +
                "LEFT JOIN mpa_rating m ON f.mpa = m.id_mpa " +
                "LEFT JOIN film_genre fg ON f.id = fg.id_film " +
                "LEFT JOIN  genre g ON fg.id_genre = g.id_genre " +
                "WHERE id = ?", id);
        return makeFilms(rowSet).get(0);
    }

    public void addLike(long filmId, long userId) {
        ValidateFilm.validateIdFilm(filmId);
        ValidateFilm.validateIdFilm(userId);
        String sql = "INSERT INTO likes VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, filmId);
        String sql2 = "UPDATE films SET rate = rate + 1 WHERE id = ?";
        jdbcTemplate.update(sql2, filmId);
    }

    public void deleteLike(long filmId, long userId) {
        ValidateFilm.validateIdFilm(filmId);
        ValidateFilm.validateIdFilm(userId);
        String sql = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sql, userId, filmId);
        String sql2 = "UPDATE films SET rate = rate - 1 WHERE id = ?";
        jdbcTemplate.update(sql2, filmId);
    }

    public List<Film> getTopFilms(int count) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT *,  FROM films f " +
                "LEFT JOIN mpa_rating mpa ON f.mpa = mpa.id_mpa " +
                "LEFT JOIN film_genre fg ON f.id = fg.id_film " +
                "LEFT JOIN  genre g ON fg.id_genre = g.id_genre " +
                "ORDER BY rate DESC LIMIT ?", count);
        return makeFilms(rs);
    }

    public Pair<Integer, String> getMpaFilms(long id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT * FROM films f " +
                "LEFT JOIN mpa_rating m ON f.mpa = m.id_mpa " +
                "LEFT JOIN film_genre fg ON f.id = fg.id_film " +
                "LEFT JOIN  genre g ON fg.id_genre = g.id_genre " +
                "WHERE id = ?", id);
        if (rowSet.next()) {
            return new Pair<>(rowSet.getInt("id_mpa"), rowSet.getString("rating"));
        }
        return null;
    }

    public List<Film> makeFilms(SqlRowSet rs) {
        HashMap<Long, Film> films = new LinkedHashMap<>();
        while (rs.next()) {
            if (!films.containsKey(rs.getLong("id"))) {
                Film film = new Film(rs.getString("name"),
                        rs.getString("description"),
                        LocalDate.parse(rs.getString("releasedate")),
                        rs.getLong("duration"),
                        rs.getInt("rate"),
                        new Pair<>(rs.getInt("mpa"), rs.getString("rating")),
                        new HashSet<>());
                film.setId(rs.getLong("id"));
                films.put(film.getId(), film);
                if (rs.getInt("id_genre") != 0 || rs.getString("genre") != null) {
                    film.setGenres(new HashSet<>(Set.of(
                            new Pair<>(rs.getInt("id_genre"), rs.getString("genre")))));
                }
            } else {
                Film film = films.get(rs.getLong("id"));
                Set<Pair<Integer, String>> genres = film.getGenres();
                genres.add(new Pair<>(rs.getInt("id_genre"), rs.getString("genre")));
                film.setGenres(film.getGenres().stream().
                        sorted(Comparator.comparing(Pair::getId)).
                        collect(Collectors.toCollection(LinkedHashSet::new)));
            }
        }
        return new LinkedList<>(films.values());
    }
}
