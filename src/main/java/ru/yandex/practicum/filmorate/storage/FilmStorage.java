package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getAllFilms();

    Film createFilm(Film film) throws ValidationException;

    Film updateFilm(Film film) throws ValidationException;

    Film getFilmById(long id);
}
