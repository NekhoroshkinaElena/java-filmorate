package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("films")
public class FilmController {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private int currentId = 1;

    private int getUniqueID() {
        return currentId++;
    }

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@RequestBody Film film) throws ValidationException {
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
        film.setId(getUniqueID());
        films.put(film.getId(), film);
        log.info("создан новый фильм");
        return films.get(film.getId());
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws ValidationException {
        if (films.get(film.getId()) == null) {
            log.info("фильма с таким id не существует");
            throw new ValidationException();
        }
        log.info("фильм успешно обновлён");
        films.replace(film.getId(), film);
        return films.get(film.getId());
    }
}
