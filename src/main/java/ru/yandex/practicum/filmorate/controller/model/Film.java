package ru.yandex.practicum.filmorate.controller.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.*;

@Data
public class Film {
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    private int rate;
    private Pair<Integer, String> mpa;
    private Set<Pair<Integer, String>> genres;

    public Film(String name, String description, LocalDate releaseDate, long duration, int rate,
                Pair<Integer, String> mpa, Set<Pair<Integer, String>> genres) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
        this.mpa = mpa;
        this.genres = genres;
    }
}
