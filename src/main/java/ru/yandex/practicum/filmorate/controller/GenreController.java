package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.controller.model.Pair;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("genres")
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public List<Pair<Integer, String>> getAllGenres() {
        return genreService.getAllGenres();
    }

    @GetMapping("/{id}")
    public Pair<Integer, String> getGenreById(@PathVariable("id") int genreId) {
        return genreService.getGenreById(genreId);
    }
}
