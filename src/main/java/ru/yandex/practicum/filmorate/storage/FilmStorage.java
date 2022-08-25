package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

/***
 * Интерфейс для хранилища фильмов.
 */
public interface FilmStorage {

    /**
     * Получает все фильмы.
     *
     * @return список всех фильмов
     */
    List<Film> getAllFilms();

    /**
     * Создаёт и добавляет фильм.
     *
     * @param film фильм
     * @return добавленный фильм
     */
    Film createFilm(Film film);

    /**
     * Обновляет фильм.
     *
     * @param film фильм
     * @return обновлённый фильм
     */
    Film updateFilm(Film film);

    /**
     * Находит фильм по id.
     *
     * @param id идентификационный номер фильма
     * @return найденный фильм
     */
    Film getFilmById(long id);
}
