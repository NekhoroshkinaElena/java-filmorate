package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.controller.model.Film;
import ru.yandex.practicum.filmorate.exeption.ValidationException;

@Slf4j
public class ValidateRatingMpa {

    public static void validateMpa(Film film) {
        if (film.getMpa() == null) {
            log.error("рейтинг MPA не может быть пустым");
            throw new ValidationException("рейтинг MPA не может быть пустым");
        }
        if (film.getMpa().getId() > 5 || film.getMpa().getId() < 0) {
            log.error("некорректный id MPA рейтинга");
            throw new ValidationException("некорректный id MPA рейтинга");
        }
    }
}
