package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Optional;

public interface GenreStorage {
    Optional<Genre> findById(Long id);
}
