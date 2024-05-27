package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FilmService {

    private final FilmStorage storage;
    private final UserService userService;

    public Collection<Film> findAll() {
        return storage.findAll();
    }

    public Optional<Film> findById(Long filmId) {
        return storage.findById(filmId);
    }

    public Film create(Film film) {
        return storage.create(film);
    }

    public Film update(Film newFilm) {
        return storage.update(newFilm);
    }

    public void like(Long id, Long userId) {
        User user = getUser(userId);
        Film film = getFilm(id);
        storage.like(film, user.getId());
    }

    public void removeLike(Long id, Long userId) {
        User user = getUser(userId);
        Film film = getFilm(id);
        storage.removeLike(film, user.getId());
    }

    private Film getFilm(Long id) {
        return storage.findById(id)
                .orElseThrow(() -> new ConditionsNotMetException(String.format("Фильм с id = %d не найден", id)));
    }

    private User getUser(Long id) {
        return userService.findById(id)
                .orElseThrow(() -> new ConditionsNotMetException(String.format("Пользователь с id = %d не найден", id)));
    }

    public List<Film> getPopular(Integer count) {
        return storage.getPopular(count);
    }
}
