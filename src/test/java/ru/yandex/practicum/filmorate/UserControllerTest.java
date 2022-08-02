package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;


@SpringBootTest
public class UserControllerTest {
    static UserController userController;

    @BeforeEach
    public void film() {
        userController = new UserController();
    }

    @Test
    void getUsers() throws ValidationException {
        User user = new User("elena@mail.ru", "Lena_nekhoroshkina", "Elena",
                LocalDate.of(1998, 7, 22));
        User user2 = new User("elena@mail2.ru", "Lena_nekhoroshkina2", "Elena2",
                LocalDate.of(1999, 7, 22));
        userController.createUser(user);
        userController.createUser(user2);
        assertEquals(List.of(user, user2), userController.findAll());
    }

    @Test
    void createUser() throws ValidationException {
        User user = new User("elena@mail.ru", "Lena_nekhoroshkina", "Elena",
                LocalDate.of(1998, 7, 22));
        assertEquals(user, userController.createUser(user));
    }

    @Test
    void EmptyEmail() throws ValidationException {
        User user = new User("", "Lena_nekhoroshkina", "Elena",
                LocalDate.of(1998, 7, 22));
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void notAValidEmailAddress() throws ValidationException {
        User user = new User("elenamail.ru", "Lena_nekhoroshkina", "Elena",
                LocalDate.of(1998, 7, 22));
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void notAValidLogin() throws ValidationException {
        User user = new User("elenamail.ru", "Lena nekhoroshkina", "Elena",
                LocalDate.of(1998, 7, 22));
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void EmptyLogin() throws ValidationException {
        User user = new User("elenamail.ru", "", "Elena",
                LocalDate.of(1998, 7, 22));
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void EmptyNameUser() throws ValidationException {
        User user = new User("elena@mail.ru", "Lena_nekhoroshkina", "",
                LocalDate.of(1998, 7, 22));
        assertEquals("Lena_nekhoroshkina", userController.createUser(user).getName());
    }

    @Test
    void dataBirthday() throws ValidationException {
        User user = new User("elena@mail.ru", "Lena_nekhoroshkina", "",
                LocalDate.of(2023, 7, 22));
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void updateUser() throws ValidationException {
        User user = new User("elena@mail.ru", "Lena_nekhoroshkina", "Elena",
                LocalDate.of(2000, 7, 22));
        userController.createUser(user);
        User userUpdate = new User("elena@mail.ruUpdate", "Lena_nekhoroshkinaUpdate", "Elena",
                LocalDate.of(2003, 7, 22));
        userUpdate.setId(user.getId());
        userController.update(userUpdate);
        assertEquals(List.of(userUpdate), userController.findAll());
    }
}
