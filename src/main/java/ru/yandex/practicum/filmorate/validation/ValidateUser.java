package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.controller.model.User;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;

import java.time.LocalDate;

@Slf4j
public class ValidateUser {

    public static void validateCreateUser(User user) {
        if (user.getLogin() == null || user.getLogin().contains(" ")
                || user.getLogin().isEmpty()) {
            log.error("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.error("Электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getId() < 0) {
            log.error("id не может быть отрицательным числом");
            throw new UserNotFoundException("id не может быть отрицательным числом");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    public static void validateUpdateUser(User user) {
        if (user.getId() < 0) {
            log.error("id не может быть отрицательным числом");
            throw new UserNotFoundException("id не может быть отрицательным числом");
        }
    }

    public static void validateIdUser(long id) {
        if (id < 0) {
            log.error("id не может быть отрицательным числом");
            throw new UserNotFoundException("id не может быть отрицательным числом");
        }
    }
}
