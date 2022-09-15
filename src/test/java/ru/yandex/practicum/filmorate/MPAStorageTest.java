package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.model.Pair;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MPAStorageTest {

    private final MPAStorage mpaStorage;

    @Test
    public void getAllMpa(){
        assertThat(mpaStorage.getAllMPA()).isEqualTo(List.of(
                new Pair<>(1, "G"),
                new Pair<>(2, "PG"),
                new Pair<>(3, "PG-13"),
                new Pair<>(4, "R"),
                new Pair<>(5, "NC-17"))
        );
    }

    @Test
    public void getMpaById(){
        assertThat(mpaStorage.getMpaById(5).name).isEqualTo("NC-17");
        assertThat(mpaStorage.getMpaById(3).name).isEqualTo("PG-13");
    }
}
