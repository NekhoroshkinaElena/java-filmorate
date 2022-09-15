//package ru.yandex.practicum.filmorate.storage;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import ru.yandex.practicum.filmorate.exeption.FilmNotFoundException;
//import ru.yandex.practicum.filmorate.exeption.ValidationException;
//import ru.yandex.practicum.filmorate.controller.model.Film;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//@Slf4j
//@Component
//public class InMemoryFilmStorage implements FilmStorage {
//    private final HashMap<Long, Film> films = new HashMap<>();
//    private int currentId = 1;
//
//    private int getUniqueID() {
//        return currentId++;
//    }
//
//    @Override
//    public List<Film> getAllFilms() {
//        return new ArrayList<>(films.values());
//    }
//
//    @Override
//    public Film createFilm(Film film) {
//        if (film.getName() == null || film.getName().isEmpty()) {
//            log.error("название не может быть пустым");
//            throw new ValidationException("название не может быть пустым");
//        }
//        if (film.getDescription() == null || film.getDescription().length() > 200) {
//            log.error("максимальная длина описания — 200 символов");
//            throw new ValidationException("максимальная длина описания — 200 символов");
//        }
//        if (film.getReleaseDate() == null
//                || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
//            log.error("дата релиза — не раньше 28 декабря 1895 года");
//            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
//        }
//        if (film.getDuration() < 0) {
//            log.error("продолжительность фильма должна быть положительной.");
//            throw new ValidationException("продолжительность фильма должна быть положительной.");
//        }
//        if (film.getId() < 0) {
//            log.error("id фильма не может быть отрицательным");
//            throw new FilmNotFoundException("id фильма не может быть отрицательным числом");
//        }
//        film.setId(getUniqueID());
//        films.put(film.getId(), film);
//        log.info("создан новый фильм");
//        return films.get(film.getId());
//    }
//
//    @Override
//    public Film updateFilm(Film film) {
//        if (film.getId() < 0) {
//            log.error("id фильма не может быть отрицательным числом");
//            throw new FilmNotFoundException("id фильма не может быть отрицательным числом");
//        }
//        if (films.get(film.getId()) == null) {
//            log.error("фильма с таким id не существует");
//            throw new FilmNotFoundException("фильм с таким id не существует");
//        }
//        log.info("фильм успешно обновлён");
//        films.replace(film.getId(), film);
//        return films.get(film.getId());
//    }
//
//    @Override
//    public Film getFilmById(long id) {
//        if (id < 0) {
//            throw new FilmNotFoundException("id фильма не может быть отрицательным числом");
//        }
//        if (films.get(id) == null) {
//            throw new FilmNotFoundException("фильм с таким id не существует");
//        }
//        return films.get(id);
//    }
//}
