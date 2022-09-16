package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.model.User;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {
    private final UserDbStorage userStorage;

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public Optional<User> getUser(long userId) {
        return userStorage.getUserById(userId);
    }

    public void addFriend(long id, long friendId) {
        userStorage.addFriend(id, friendId);
    }

    public List<User> getListFriends(Long id) {
        return userStorage.getListFriends(id);
    }

    public void deleteFriend(long userId, long friendId) {
        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getListCommonFriends(long id, long otherId) {
        return userStorage.getListCommonFriends(id, otherId);
    }
}
