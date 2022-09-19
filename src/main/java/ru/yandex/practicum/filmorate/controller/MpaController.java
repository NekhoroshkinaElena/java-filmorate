package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.controller.model.Pair;
import ru.yandex.practicum.filmorate.service.MPAService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("mpa")
public class MpaController {
    private final MPAService mpaService;

    @GetMapping
    public List<Pair<Integer, String>> getAllMpa() {
        return mpaService.getAllMPA();
    }

    @GetMapping("/{id}")
    public Pair<Integer, String> getMpaById(@PathVariable("id") int mpaId) {
        return mpaService.getMPAById(mpaId);
    }
}
