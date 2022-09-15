package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.controller.model.Pair;

import java.util.List;

public interface MPAStorage {

    List<Pair> getAllMPA();

    Pair<Integer, String> getMpaById(int genreId);
}
