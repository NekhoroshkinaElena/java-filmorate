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
import java.util.*;

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
        createTestFilm("name 1", "description 1 ", 2000, 100, 6, 1, 2);
        createTestFilm("name 2", "description 2 ", 2001, 110, 5, 2, 3);
        assertThat(filmStorage.getAllFilms().size()).isEqualTo(2);
        assertThat(filmStorage.getAllFilms().get(0).getName()).isEqualTo("name 1");
        assertThat(filmStorage.getAllFilms().get(1).getName()).isEqualTo("name 2");
    }

    @Test
    void createFilmCorrect() {
        Film film = createTestFilm("correct film", "description",
                2000, 100, 6, 1, 1);
        Long idFilm = film.getId();
        assertThat(idFilm).isEqualTo(1L);
        assertThat(filmStorage.getFilmById(idFilm).getName()).isEqualTo("correct film");
        assertThat(filmStorage.getFilmById(idFilm).getDescription()).isEqualTo("description");
        assertThat(filmStorage.getFilmById(idFilm).getReleaseDate()).isEqualTo(
                LocalDate.of(2000, 1, 1));
        assertThat(filmStorage.getFilmById(idFilm).getDuration()).isEqualTo(100);
        assertThat(filmStorage.getFilmById(idFilm).getMpa()).isEqualTo(new Pair<>(1, "G"));
        assertThat(filmStorage.getFilmById(idFilm).getGenres()).isEqualTo(Set.of(new Pair<>(1, "Комедия")));
    }

    @Test
    void getFilmById() {
        createTestFilm("film 1", "description 1", 2000, 110, 5, 1, 1);
        assertThat(filmStorage.getFilmById(1).getName()).isEqualTo("film 1");
        assertThat(filmStorage.getFilmById(1).getDescription()).isEqualTo("description 1");
        assertThat(filmStorage.getFilmById(1).getReleaseDate()).isEqualTo(
                LocalDate.of(2000, 1, 1));
        assertThat(filmStorage.getFilmById(1).getDuration()).isEqualTo(110);
        assertThat(filmStorage.getFilmById(1).getMpa()).isEqualTo(new Pair<>(1, "G"));
        assertThat(filmStorage.getFilmById(1).getGenres()).isEqualTo(Set.of(new Pair<>(1, "Комедия")));
    }

    @Test
    void createFilmIncorrectId() {
        Pair<Integer, String> pair = new Pair<>(1, "id");
        LinkedHashSet<Pair<Integer, String>> genres = new LinkedHashSet<>();
        genres.add(pair);
        Film film = new Film("film 1", "description 1", LocalDate.of(2000, 1, 1),
                110, 5, new Pair<>(1, "id"), genres);
        film.setId(-1);
        assertThrows(ValidationException.class, () -> filmStorage.createFilm(film));
    }

    @Test
    void createFilmWithIncorrectName() {
        assertThrows(ValidationException.class, () -> createTestFilm("", "description",
                2000, 100, 5, 1, 1));
    }

    @Test
    void createFilmWithIncorrectDescription() {
        assertThrows(ValidationException.class, () -> createTestFilm("name",
                "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33" +
                        "34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 57 58 59 60 61 62 63" +
                        "64 65 67 68 69 70 71 72 73 74 75 76 77 78 79 80 81 82 83 84 85 86 87 89 90 91 92 93 94 95",
                2000, 100, 5, 1, 1));
    }

    @Test
    void createFilmWithIncorrectDate() {
        assertThrows(ValidationException.class, () -> createTestFilm("name", "description",
                1700, 100, 5, 1, 1));
    }

    @Test
    void createFilmWithIncorrectDuration() {
        assertThrows(ValidationException.class, () -> createTestFilm("name", "description",
                2000, -100, 5, 1, 1));
    }

    @Test
    void createFilmWithIncorrectMPA() {
        assertThrows(ValidationException.class, () -> createTestFilm("name", "description",
                2000, 100, 5, -1, 1));
    }

    @Test
    void updateFilm() {
        Film film = createTestFilm("name", "description",
                2000, 120, 7, 1, 1);
        Film film2 = new Film("film update", "description update",
                LocalDate.of(2001, 2, 2), 100, 8,
                new Pair<>(3, "id"), new LinkedHashSet<>(List.of(new Pair<>(3, "id"))));
        film2.setId(1L);
        filmStorage.updateFilm(film2);
        Film filmUpdate = filmStorage.getFilmById(film.getId());
        assertThat(filmUpdate.getId()).isEqualTo(1);
        assertThat(filmUpdate.getName()).isEqualTo("film update");
        assertThat(filmUpdate.getDescription()).isEqualTo("description update");
        assertThat(filmUpdate.getReleaseDate()).isEqualTo(LocalDate.of(2001, 2, 2));
        assertThat(filmUpdate.getDuration()).isEqualTo(100);
        assertThat(filmUpdate.getRate()).isEqualTo(8);
        assertThat(filmUpdate.getMpa()).isEqualTo(new Pair<>(3, "PG-13"));
        assertThat(filmUpdate.getGenres()).isEqualTo(Set.of(new Pair<>(3, "Мультфильм")));
    }

    @Test
    void addLike() {
        createTestUser("user1@mail.ru", 1990);
        createTestUser("user2@mail.ru", 1998);
        Film film = createTestFilm("film", "+like", 2000, 100, 5, 1, 1);
        filmStorage.addLike(1, 1);
        filmStorage.addLike(1, 2);
        assertThat(filmStorage.getFilmById(film.getId()).getRate()).isEqualTo(7);
    }

    @Test
    void deleteLike() {
        User user1 = createTestUser("user1@mail.ru", 1990);
        User user2 = createTestUser("user2@mail.ru", 1998);
        Film film = createTestFilm("film", "-like", 2000, 100, 10, 1, 1);
        filmStorage.deleteLike(film.getId(), user1.getId());
        filmStorage.deleteLike(film.getId(), user2.getId());
        assertThat(filmStorage.getFilmById(film.getId()).getRate()).isEqualTo(8);
    }

    @Test
    void getTopFilms() {
        Film film = createTestFilm("film 1", "top", 2000, 100, 6, 1, 1);
        Film film2 = createTestFilm("film 2", "top2", 1998, 99, 9, 2, 2);
        Film film3 = createTestFilm("film 3", "top3", 1999, 98, 8, 3, 3);
        assertThat(filmStorage.getTopFilms(2).get(0).getName()).isEqualTo("film 2");
        assertThat(filmStorage.getTopFilms(2).get(1).getName()).isEqualTo("film 3");
        assertThat(filmStorage.getTopFilms(3).get(2).getName()).isEqualTo("film 1");
    }

    public Film createTestFilm(String name, String description, int year, int duration, int rate, int idMpa, int idGenre) {
        Pair<Integer, String> mpa = new Pair<>(idMpa, "id");
        Pair<Integer, String> genre = new Pair<>(idGenre, "id");
        Set<Pair<Integer, String>> genres = new LinkedHashSet<>();
        genres.add(genre);
        Film film = new Film(name, description, LocalDate.of(year, 1, 1),
                duration, rate, mpa, genres);
        return filmStorage.createFilm(film);
    }

    public User createTestUser(String email, int year) {
        User user = new User(email, "login", "name", LocalDate.of(year, 1, 1));
        return userDbStorage.createUser(user);
    }
}
