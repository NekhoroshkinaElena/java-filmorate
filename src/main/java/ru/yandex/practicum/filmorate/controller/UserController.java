package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
public class UserController {
    private HashMap<Integer, User> users = new HashMap<>();
    int currentId = 1;

    private int getUniqueID() {
        return currentId++;
    }

    @GetMapping("users")
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping("users")
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        if (users.containsValue(user)) {
            log.info("Пользователь уже существует");
            throw new ValidationException("Пользователь уже существует");
        } else if (user.getLogin().contains(" ")) {
            log.info("логин не может быть пустым и содержать пробелы");
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        } else if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.info("электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
        } else if (user.getLogin().isEmpty() || user.getLogin().isBlank()) {
            log.info("логин не может быть пустым и содержать пробелы");
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("дата рождения не может быть в будущем");
            throw new ValidationException("дата рождения не может быть в будущем");
        }
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(getUniqueID());
        users.put(user.getId(), user);
        log.info("создан новый пользователь");
        return users.get(user.getId());
    }

    @PutMapping("users")
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