package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;


@SpringBootTest
public class FilmControllerTest {
    static FilmController filmController;

    @BeforeEach
    public void film() {
        filmController = new FilmController();
    }

    @Test
    void getFilms() throws ValidationException {
        Film film = new Film("Film", "description",
                LocalDate.of(2015, 9, 13), 60, 2);
        Film film2 = new Film("Film2", "description2",
                LocalDate.of(2016, 9, 13), 60, 2);
        filmController.create(film);
        filmController.create(film2);
        assertEquals(List.of(film, film2), filmController.findAll());
    }

    @Test
    void createFilm() throws ValidationException {
        Film film = new Film("Film", "description",
                LocalDate.of(2015, 9, 13), 60, 2);
        assertEquals(film, filmController.create(film));
    }

    @Test
    void untitledMovie() throws ValidationException {
        Film film = new Film("", "description",
                LocalDate.of(2015, 9, 13), 60, 2);
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void maxLengthDescription() throws ValidationException {
        Film film = new Film("film", "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20," +
                "21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40," +
                "41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60," +
                "61,62,63,64,65,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81," +
                "82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100.",
                LocalDate.of(2015, 9, 13), 60, 2);
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void releaseDate() throws ValidationException {
        Film film = new Film("oldFilm", "description",
                LocalDate.of(1894, 9, 13), 60, 2);
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void duration() throws ValidationException {
        Film film = new Film("oldFilm", "description",
                LocalDate.of(1994, 9, 13), -60, 2);
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void updateFilm() throws ValidationException {
        Film film = new Film("oldFilm", "description",
                LocalDate.of(1994, 9, 13), 60, 2);
        filmController.create(film);
        Film filmUpdate = new Film("newFilm", "description",
                LocalDate.of(2020, 5, 7), 120, 5);
        filmUpdate.setId(film.getId());
        filmController.update(filmUpdate);
        assertEquals(List.of(filmUpdate), filmController.findAll());

    }
}
