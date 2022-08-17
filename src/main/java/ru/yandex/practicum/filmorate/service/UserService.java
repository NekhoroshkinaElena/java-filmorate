package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(long id, long friendId) throws UserNotFoundException {
        if (id < 0 || friendId < 0) {
            log.error("id не может быть отрицательным числом");
            throw new UserNotFoundException("id не может быть отрицательным числом");
        }
        userStorage.getUser(id).addFriend(friendId);
        userStorage.getUser(friendId).addFriend(id);
    }

    public List<User> getListFriends(Long id) throws UserNotFoundException {
        if (id < 0) {
            log.error("id не может быть отрицательным числом");
            throw new UserNotFoundException("id не может быть отрицательным числом");
        }
        List<User> friends = new ArrayList<>();
        for (Long i : userStorage.getUser(id).getFriends()) {
            friends.add(userStorage.getUser(i));
        }
        return friends;
    }

    public void deleteFriend(long userId, long friendId) throws UserNotFoundException {
        if (userId < 0 || friendId < 0) {
            log.error("id не может быть отрицательным числом");
            throw new UserNotFoundException("id не может быть отрицательным числом");
        }
        userStorage.getUser(userId).deleteFriend(friendId);
        userStorage.getUser(friendId).deleteFriend(userId);
    }

    public List<User> getListCommonFriends(long id, long otherId) throws UserNotFoundException {
        List<User> commonFriends = new ArrayList<>();
        for (long i : userStorage.getUser(id).getFriends()) {
            if (userStorage.getUser(otherId).getFriends().contains(i)) {
                commonFriends.add(userStorage.getUser(i));
            }
        }
        return commonFriends;
    }
}