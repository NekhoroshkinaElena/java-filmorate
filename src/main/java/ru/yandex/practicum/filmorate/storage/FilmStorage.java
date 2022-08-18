package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

/***
 * Интерфейс для хранилища фильмов
 */
public interface FilmStorage {

    /**
     * получает все фильмы
     *
     * @return список всех фильмов
     */
    List<Film> getAllFilms();

    /**
     * создаёт и добавляет фильм
     *
     * @param film фильм
     * @return добавленный фильм
     */
    Film createFilm(Film film);

    /**
     * обновляет фильма
     *
     * @param film фильм
     * @return обновлённый фильм
     */
    Film updateFilm(Film film);

    /**
     * находит фильм по id
     *
     * @param id идентификационный номер фильма
     * @return найденный фильм
     */
    Film getFilmById(long id);
}
