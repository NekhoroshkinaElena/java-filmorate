package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.controller.model.Film;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
public class ValidateFilm {

    public static void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            log.error("название не может быть пустым");
            throw new ValidationException("название не может быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.error("максимальная длина описания — 200 символов");
            throw new ValidationException("максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() == null
                || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.error("продолжительность фильма должна быть положительной.");
            throw new ValidationException("продолжительность фильма должна быть положительной.");
        }
        if (film.getId() < 0) {
            log.error("id фильма не может быть отрицательным");
            throw new ValidationException("id фильма не может быть отрицательным числом");
        }
    }

    public static void validateFilmUpdate(Film film) {
        if (film.getId() < 0) {
            log.error("id фильма не может быть отрицательным числом");
            throw new FilmNotFoundException("id фильма не может быть отрицательным числом");
        }
        if (film.getDescription().length() > 200) {
            log.error("максимальная длина описания — 200 символов");
            throw new ValidationException("максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.error("продолжительность фильма должна быть положительной.");
            throw new ValidationException("продолжительность фильма должна быть положительной.");
        }
    }

    public static void validateIdFilm(long id) {
        if (id < 0) {
            log.error("id фильма не может быть отрицательным числом");
            throw new FilmNotFoundException("id фильма не может быть отрицательным числом");
        }
    }
}
