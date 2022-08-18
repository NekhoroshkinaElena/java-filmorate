package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("films")
public class FilmController {
    private final FilmService filmService;
    private final FilmStorage inMemoryFilmStorage;

    @GetMapping
    public List<Film> findAll() {
        return inMemoryFilmStorage.getAllFilms();
    }

    @PostMapping
    public Film create(@RequestBody Film film) throws ValidationException {
        return inMemoryFilmStorage.createFilm(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws ValidationException {
        return inMemoryFilmStorage.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable("id") long userId) {
        return inMemoryFilmStorage.getFilmById(userId);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") long filmId, @PathVariable("userId") long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") long filmId, @PathVariable("userId") long userId) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilm(@RequestParam(defaultValue = "10") int count) {
        return filmService.getTopFilms(count);
    }
}
