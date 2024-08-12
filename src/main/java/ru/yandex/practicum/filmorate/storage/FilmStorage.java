package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortOrderFilmsByDirector;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> findAll();

    Optional<Film> findFilmById(Long filmId);

    Film save(Film film);

    Film update(Film film);

    Long like(Film film, Long userId);

    boolean hasLike(Film film, Long userId);

    void removeLike(Film film, Long userId);

    void removeFilm(Film film);

    List<Film> getPopular(Long count, Long genreId, Long year);

    List<Film> findRecommendations(Long userId);

    List<Film> getCommon(Long userId, Long friendId);

    List<Film> getDirectorFilms(Long directorId, SortOrderFilmsByDirector sortBy);
}
