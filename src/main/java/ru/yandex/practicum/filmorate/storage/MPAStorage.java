package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.controller.model.Pair;

import java.util.List;

/***
 * Интерфейс для хранилища MPA рейтинга.
 */
public interface MPAStorage {

    /**
     * Получает все рейтинги MPA.
     *
     * @return список всех рейтингов MPA
     */
    List<Pair<Integer, String>> getAllMPA();

    /**
     * Находит рейтинг по id.
     *
     * @param idMpa идентификационный номер рейтинга
     * @return номер рейтинга и его название
     */
    Pair<Integer, String> getMpaById(int idMpa);
}
