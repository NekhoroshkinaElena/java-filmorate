package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    private int id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final long duration;
    private int rate;

    public Film(String name, String description, LocalDate releaseDate, long duration, int rate) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
    }
}