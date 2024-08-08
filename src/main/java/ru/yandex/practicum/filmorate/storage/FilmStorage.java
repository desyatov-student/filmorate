package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> findAll();

    Optional<Film> findFilmById(Long filmId);

    Film save(Film film);

    Film update(Film film);

    void like(Film film, Long userId);

    boolean hasLike(Film film, Long userId);

    void removeLike(Film film, Long userId);

    List<Film> getPopular(Integer count);

    List<Film> search(String title, String director);
}
