package ru.yandex.practicum.filmorate.controller.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
public class Pair<T, U> {
    public final T id;
    public final U name;

    public Pair(T id, U name) {
        this.id = id;
        this.name = name;
    }
}
