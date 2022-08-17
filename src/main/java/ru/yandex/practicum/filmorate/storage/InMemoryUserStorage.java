package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> users = new HashMap<>();
    int currentId = 1;

    private int getUniqueID() {
        return currentId++;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) throws ValidationException {
        if (users.containsValue(user)) {
            log.error("Пользователь уже существует");
            throw new ValidationException("Пользователь уже существует");
        }
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
        user.setId(getUniqueID());
        users.put(user.getId(), user);
        log.info("создан новый пользователь");
        return users.get(user.getId());
    }

    @Override
    public User updateUser(User user) throws ValidationException {
        if (user.getId() < 0) {
            log.error("id не может быть отрицательным числом");
            throw new UserNotFoundException("id не может быть отрицательным числом");
        }
        if (users.get(user.getId()) == null) {
            log.error("пользователя с таким id не существует");
            throw new ValidationException("пользователя с таким id не существует");
        }
        log.info("данные успешно обновлены");
        users.replace(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User getUser(long id) throws UserNotFoundException {
        if (id < 0) {
            log.error("id не может быть отрицательным числом");
            throw new UserNotFoundException("id не может быть отрицательным числом");
        }
        if (users.get(id) == null) {
            log.error("Пользователь с id = " + id + " не найден");
            throw new UserNotFoundException("Пользователь с id = " + id + " не найден");
        }
        return users.get(id);
    }
}
