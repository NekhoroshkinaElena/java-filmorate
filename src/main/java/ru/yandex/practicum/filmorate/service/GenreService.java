package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.model.Pair;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Pair<Integer, String>> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Pair<Integer, String> getGenreById(int genreId) {
        return genreStorage.getGenreById(genreId);
    }
}
