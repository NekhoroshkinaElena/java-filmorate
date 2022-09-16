package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.controller.model.Film;

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

    /**
     * Добавляет лайк фильму.
     *
     * @param filmId идентификационный номер фильма
     * @param userId идентификационный номер пользователя
     */
    void addLike(long filmId, long userId);//добавить документацию

    /**
     * Удаляет лайк у фильма.
     *
     * @param filmId идентификационный номер фильма
     * @param userId идентификационный номер пользователя
     */
    void deleteLike(long filmId, long userId);//добавить документацию

    /**
     * Получает топ N фильмов.
     *
     * @param count количество фильмов возвращаемых пользователю
     * @return список фильмов с наивысшей оценкой
     */
    public List<Film> getTopFilms(int count);//добавить документацию
}
