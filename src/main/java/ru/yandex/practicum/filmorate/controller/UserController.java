package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("users")
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();
    int currentId = 1;

    private int getUniqueID() {
        return currentId++;
    }

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws ValidationException {
        if (users.containsValue(user)) {
            log.error("Пользователь уже существует");
            throw new ValidationException("Пользователь уже существует");
        }
        if (user.getLogin() == null || user.getLogin().contains(" ")
                || user.getLogin().isEmpty()) {
            log.error("логин не может быть пустым и содержать пробелы");
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.error("электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("дата рождения не может быть в будущем");
            throw new ValidationException("дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(getUniqueID());
        users.put(user.getId(), user);
        log.info("создан новый пользователь");
        return users.get(user.getId());
    }

    @PutMapping
    public User update(@RequestBody User user) throws ValidationException {
        if (users.get(user.getId()) == null) {
            log.info("такого пользователя не существует");
            throw new ValidationException();
        }
        log.info("данные успешно обновлены");
        users.replace(user.getId(), user);
        return users.get(user.getId());
    }
}
