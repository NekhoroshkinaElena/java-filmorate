package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.model.Pair;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class MPAService {

    private final MPAStorage mpaStorage;

    public List<Pair> getAllMPA() {
        return mpaStorage.getAllMPA();
    }

    public Pair<Integer, String> getMPAById(int mpaId) {
        return mpaStorage.getMpaById(mpaId);
    }
}
