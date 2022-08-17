package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(long filmId, long userId) {
        if (filmId < 0 || userId < 0) {
            log.error("id не может быть отрицательным числом");
            throw new UserNotFoundException("id не может быть отрицательным числом");
        }
        filmStorage.getFilmById(filmId).addLike(userId);
    }

    public void deleteLike(long filmId, long userId) {
        if (filmId < 0 || userId < 0) {
            log.error("id не может быть отрицательным числом");
            throw new UserNotFoundException("id не может быть отрицательным числом");
        }
        filmStorage.getFilmById(filmId).deleteLike(userId);
    }

    public List<Film> getTop10Films(int count) {
        List<Film> sortedList = new LinkedList<>(filmStorage.getAllFilms());
        return sortedList.stream()
                .sorted((o1, o2) -> {
                    if (o1.getRate() == o2.getRate())
                        return o2.getName().compareTo(o1.getName());
                    else if (o1.getRate() > o2.getRate())
                        return -1;
                    else return 1;
                })
                .limit(count)
                .collect(Collectors.toList());
    }
}
