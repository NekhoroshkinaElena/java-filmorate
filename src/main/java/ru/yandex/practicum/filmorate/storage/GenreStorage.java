package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.controller.model.Pair;

import java.util.List;

/***
 * Интерфейс для хранилища жанров.
 */
public interface GenreStorage {

    /**
     * Получает все жанры.
     *
     * @return список всех жанров
     */
    List<Pair<Integer, String>> getAllGenres();

    /**
     * Находит жанр по id.
     *
     * @param genreId идентификационный номер жанра
     * @return номер жанра и его название
     */
    Pair<Integer, String> getGenreById(int genreId);
}
