package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.controller.model.Pair;

import java.util.List;

public interface GenreStorage {

     List<Pair> getAllGenres();

    Pair<Integer, String> getGenreById(int genreId);
}
