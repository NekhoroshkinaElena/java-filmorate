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
import ru.yandex.practicum.filmorate.storage.impl.GenreDbStorage;
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
public class GenreStorageTest {

    private final GenreDbStorage genreDbStorage;

    @Test
    public void getAllGenres(){
        assertThat(genreDbStorage.getAllGenres()).isEqualTo(List.of(
                new Pair<>(1, "Комедия"),
                new Pair<>(2, "Драма"),
                new Pair<>(3, "Мультфильм"),
                new Pair<>(4, "Триллер"),
                new Pair<>(5, "Документальный"),
                new Pair<>(6, "Боевик"))
        );
    }

    @Test
    public void getGenreById(){
        assertThat(genreDbStorage.getGenreById(3).name).isEqualTo("Мультфильм");
    }
}
