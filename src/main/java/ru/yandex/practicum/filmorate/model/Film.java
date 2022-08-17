package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    private int rate;
    private Set<Long> likes = new HashSet<>();

    public Film(String name, String description, LocalDate releaseDate, long duration, int rate) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
    }

    public void addLike(long userId) {
        likes.add(userId);
    }

    public void deleteLike(long userId) {
        likes.remove(userId);
    }
}
