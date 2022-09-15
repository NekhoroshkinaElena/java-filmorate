package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.model.Film;
import ru.yandex.practicum.filmorate.controller.model.Pair;
import ru.yandex.practicum.filmorate.controller.model.User;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmStorageTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userDbStorage;

    @Test
    void getAllFilms() {
        Pair pair = new Pair<>(1, "id");
        Pair pair2 = new Pair<>(2, "id");
        List<Pair<Integer, String>> genres = new ArrayList<>();
        List<Pair<Integer, String>> genres2 = new ArrayList<>();
        genres.add(pair);
        genres2.add(pair2);
        Film film = new Film("film 1", "description 1", LocalDate.of(2000, 1, 1),
                110, 5, new Pair<>(1, "id"), genres);
        Film film2 = new Film("film 2", "description 2", LocalDate.of(2020, 2, 2),
                110, 6, new Pair<>(2, "id"), genres2);
        filmStorage.createFilm(film);
        filmStorage.createFilm(film2);
        assertThat(filmStorage.getAllFilms().size()).isEqualTo(2);
    }

    @Test
    void createFilmCorrect() {//упростить
        Pair pair = new Pair<>(1, "id");
        List<Pair<Integer, String>> genres = new ArrayList<>();
        genres.add(pair);
        Film film = new Film("film 1", "description 1", LocalDate.of(2000, 1, 1),
                110, 5, new Pair<>(1, "id"), genres);
        Long idFilm = filmStorage.createFilm(film).getId();
        Film film1 = filmStorage.getFilmById(idFilm);
        assertThat(film1.getId()).isEqualTo(1L);
        assertThat(filmStorage.getFilmById(idFilm).getName()).isEqualTo("film 1");
        assertThat(filmStorage.getFilmById(idFilm).getDescription()).isEqualTo("description 1");
        assertThat(filmStorage.getFilmById(idFilm).getReleaseDate()).isEqualTo(
                LocalDate.of(2000, 1, 1));
        assertThat(filmStorage.getFilmById(idFilm).getDuration()).isEqualTo(110);
        assertThat(filmStorage.getFilmById(idFilm).getMpa()).isEqualTo(new Pair<>(1, "G"));
        assertThat(filmStorage.getFilmById(idFilm).getGenres()).isEqualTo(List.of(new Pair<>(1, "Комедия")));
        System.out.println(film1);
    }

    @Test
    void getFilmById(){
        Pair pair = new Pair<>(1, "id");
        List<Pair<Integer, String>> genres = new ArrayList<>();
        genres.add(pair);
        Film film = new Film("film 1", "description 1", LocalDate.of(2000, 1, 1),
                110, 5, new Pair<>(1, "id"), genres);
        filmStorage.createFilm(film);
        assertThat(filmStorage.getFilmById(1).getName()).isEqualTo("film 1");//заменить все длинные создания
        assertThat(filmStorage.getFilmById(1).getDescription()).isEqualTo("description 1");
        assertThat(filmStorage.getFilmById(1).getReleaseDate()).isEqualTo(
                LocalDate.of(2000, 1, 1));
        assertThat(filmStorage.getFilmById(1).getDuration()).isEqualTo(110);
        assertThat(filmStorage.getFilmById(1).getMpa()).isEqualTo(new Pair<>(1, "G"));
        assertThat(filmStorage.getFilmById(1).getGenres()).isEqualTo(List.of(new Pair<>(1, "Комедия")));

    }

    @Test
    void createFilmIncorrectId() {
        Pair pair = new Pair<>(1, "id");
        List<Pair<Integer, String>> genres = new ArrayList<>();
        genres.add(pair);
        Film film = new Film("film 1", "description 1", LocalDate.of(2000, 1, 1),
                110, 5, new Pair<>(1, "id"), genres);
        film.setId(-1);
        assertThrows(ValidationException.class, () -> filmStorage.createFilm(film));

    }

    @Test
    void createFilmWithIncorrectName() {
        Pair pair = new Pair<>(1, "id");
        List<Pair<Integer, String>> genres = new ArrayList<>();
        genres.add(pair);
        Film film = new Film("", "description 1", LocalDate.of(2000, 1, 1),
                110, 5, new Pair<>(1, "id"), genres);
        assertThrows(ValidationException.class, () -> filmStorage.createFilm(film));
    }

    @Test
    void createFilmWithIncorrectDescription() {
        Pair pair = new Pair<>(1, "id");
        List<Pair<Integer, String>> genres = new ArrayList<>();
        genres.add(pair);
        String description = "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 " +
                "32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 57 58 59 60 61 62 63 64" +
                " 65 67 68 69 70 71 72 73 74 75 76 77 78 79 80 81 82 83 84 85 86 87 89 90 91 92 93 94 95 96 97 98 99";
        Film film = new Film("film 1", description, LocalDate.of(2000, 1, 1),
                110, 5, new Pair<>(1, "id"), genres);
        assertThrows(ValidationException.class, () -> filmStorage.createFilm(film));
    }

    @Test
    void createFilmWithIncorrectDate() {
        Pair pair = new Pair<>(1, "id");
        List<Pair<Integer, String>> genres = new ArrayList<>();
        genres.add(pair);
        Film film = new Film("film 1", "description", LocalDate.of(1800, 1, 1),
                110, 5, new Pair<>(1, "id"), genres);
        assertThrows(ValidationException.class, () -> filmStorage.createFilm(film));
    }

    @Test
    void createFilmWithIncorrectDuration() {
        Pair pair = new Pair<>(1, "id");
        List<Pair<Integer, String>> genres = new ArrayList<>();
        genres.add(pair);
        Film film = new Film("film 1", "description", LocalDate.of(2000, 1, 1),
                -200, 5, new Pair<>(1, "id"), genres);
        assertThrows(ValidationException.class, () -> filmStorage.createFilm(film));
    }

    @Test
    void createFilmWithIncorrectMPA() {
        Pair pair = new Pair<>(1, "id");
        List<Pair<Integer, String>> genres = new ArrayList<>();
        genres.add(pair);
        Film film = new Film("film 1", "description", LocalDate.of(2000, 1, 1),
                100, 5, new Pair<>(-1, "id"), genres);
        assertThrows(ValidationException.class, () -> filmStorage.createFilm(film));

        Film film2 = new Film("film 1", "description", LocalDate.of(2000, 1, 1),
                100, 5, null, genres);
        assertThrows(ValidationException.class, () -> filmStorage.createFilm(film2));
    }

    @Test
    void updateFilm(){
        Pair pair = new Pair<>(1, "id");
        List<Pair<Integer, String>> genres = new ArrayList<>();
        genres.add(pair);
        Film film = new Film("film 1", "description", LocalDate.of(2000, 1, 1),
                100, 5, new Pair<>(1, "id"), genres);
        filmStorage.createFilm(film);

        Pair pair2 = new Pair<>(3, "id");
        genres.add(pair2);
        Film filmUpdate = new Film("film update", "description update", LocalDate.of(2000, 1, 1),
                120, 8, new Pair<>(3, "id"), genres);
        filmUpdate.setId(1L);
        filmStorage.updateFilm(filmUpdate);
        assertThat(filmStorage.getFilmById(film.getId()).getId()).isEqualTo(1);
        assertThat(filmStorage.getFilmById(film.getId()).getName()).isEqualTo("film update");
        assertThat(filmStorage.getFilmById(film.getId()).getDescription()).isEqualTo("description update");
        assertThat(filmStorage.getFilmById(film.getId()).getReleaseDate()).isEqualTo(LocalDate.of(2000, 1, 1));
        assertThat(filmStorage.getFilmById(film.getId()).getDuration()).isEqualTo(120);
        assertThat(filmStorage.getFilmById(film.getId()).getRate()).isEqualTo(8);
        assertThat(filmStorage.getFilmById(film.getId()).getMpa()).isEqualTo(new Pair<>(3, "PG-13"));
        assertThat(filmStorage.getFilmById(film.getId()).getGenres()).isEqualTo(List.of(new Pair<>(1, "Комедия"),
                new Pair<>(3, "Мультфильм")));
    }

    @Test
    void addLike(){
        User user = new User("email@ya.ru", "login", "name", LocalDate.of(2021, 8, 22));
        User user2 = new User("email@ya2.ru", "login2", "name2", LocalDate.of(2020, 8, 22));
        userDbStorage.createUser(user);
        userDbStorage.createUser(user2);
        Pair pair = new Pair<>(1, "id");
        List<Pair<Integer, String>> genres = new ArrayList<>();
        genres.add(pair);
        Film film = new Film("film 1", "description 1", LocalDate.of(2000, 1, 1),
                110, 5, new Pair<>(1, "id"), genres);
        Long idFilm = filmStorage.createFilm(film).getId();
        filmStorage.addLike(1, 1);
        filmStorage.addLike(1, 2);
        assertThat(filmStorage.getFilmById(1).getRate()).isEqualTo(7);
    }

    @Test
    void deleteLike(){
        User user = new User("email@ya.ru", "login", "name", LocalDate.of(2021, 8, 22));
        User user2 = new User("email@ya2.ru", "login2", "name2", LocalDate.of(2020, 8, 22));
        userDbStorage.createUser(user);
        userDbStorage.createUser(user2);
        Pair pair = new Pair<>(1, "id");
        List<Pair<Integer, String>> genres = new ArrayList<>();
        genres.add(pair);
        Film film = new Film("film 1", "description 1", LocalDate.of(2000, 1, 1),
                110, 5, new Pair<>(1, "id"), genres);
        Long idFilm = filmStorage.createFilm(film).getId();
        filmStorage.deleteLike(1, 1);
        filmStorage.deleteLike(1, 2);
        assertThat(filmStorage.getFilmById(1).getRate()).isEqualTo(3);
    }

    @Test
    void getTopFilms(){
        Pair pair = new Pair<>(1, "id");
        Pair pair2 = new Pair<>(2, "id");
        List<Pair<Integer, String>> genres = new ArrayList<>();
        List<Pair<Integer, String>> genres2 = new ArrayList<>();
        genres.add(pair);
        genres2.add(pair2);
        Film film = new Film("film 1", "description 1", LocalDate.of(2000, 1, 1),
                110, 5, new Pair<>(1, "id"), genres);
        Film film2 = new Film("film 2", "description 2", LocalDate.of(2020, 2, 2),
                110, 6, new Pair<>(2, "id"), genres2);
        filmStorage.createFilm(film);
        filmStorage.createFilm(film2);
        assertThat(filmStorage.getTopFilms(2).get(0).getName()).isEqualTo("film 2");
        assertThat(filmStorage.getTopFilms(2).get(1).getName()).isEqualTo("film 1");
    }
}
