package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(long userId) {
        return filmStorage.getFilmById(userId);
    }

    public void addLike(long filmId, long userId) {
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getTopFilms(count);
    }
}
