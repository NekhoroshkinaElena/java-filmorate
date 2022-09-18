package ru.yandex.practicum.filmorate.controller.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private long id;
    private String name;
    private String email;
    private String login;
    private LocalDate birthday;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
