package ru.yandex.practicum.filmorate.exeption;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
