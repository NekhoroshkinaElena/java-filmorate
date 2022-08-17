package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController {
    private final UserStorage inMemoryUserStorage;
    private final UserService userService;

    @GetMapping
    public List<User> findAll() {
        return inMemoryUserStorage.getUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws ValidationException {
        return inMemoryUserStorage.createUser(user);
    }

    @PutMapping
    public User update(@RequestBody User user) throws ValidationException {
        return inMemoryUserStorage.updateUser(user);
    }

    @GetMapping("{id}")
    public User userGet(@PathVariable("id") long userId) {
        return inMemoryUserStorage.getUser(userId);
    }


    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") long userId, @PathVariable("friendId") long friendId) {
        userService.addFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> addFriend(@PathVariable("id") long id) {
        return userService.getListFriends(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") long userId, @PathVariable("friendId") long friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getListCommonFriends(@PathVariable("id") long id, @PathVariable("otherId") long otherId) {
        return userService.getListCommonFriends(id, otherId);
    }
}
