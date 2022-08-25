package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> findAllUsers() {
        return userService.getUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws ValidationException {
        return userService.createUser(user);
    }

    @PutMapping
    public User update(@RequestBody User user) throws ValidationException {
        return userService.updateUser(user);
    }

    @GetMapping("{id}")
    public User userGet(@PathVariable("id") long userId) {
        return userService.getUser(userId);
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
