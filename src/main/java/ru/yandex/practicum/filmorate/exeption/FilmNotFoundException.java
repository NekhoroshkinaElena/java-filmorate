package ru.yandex.practicum.filmorate.exeption;

public class FilmNotFoundException extends RuntimeException {

    public FilmNotFoundException(String message) {
        super(message);
    }
}
