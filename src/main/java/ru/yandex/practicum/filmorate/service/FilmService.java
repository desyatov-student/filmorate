package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage storage;
    private final UserService userService;

    public Collection<Film> findAll() {
        return storage.findAll();
    }

    public Film findById(Long filmId) {
        return storage.findById(filmId)
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с id = %d не найден", filmId)));
    }

    public Film create(Film film) {
        return storage.create(film);
    }

    public Film update(Film newFilm) {
        return storage.update(newFilm);
    }

    public void like(Long id, Long userId) {
        User user = findUserById(userId);
        Film film = findById(id);
        storage.like(film, user.getId());
    }

    public void removeLike(Long id, Long userId) {
        User user = findUserById(userId);
        Film film = findById(id);
        storage.removeLike(film, user.getId());
    }

    private User findUserById(Long id) {
        return userService.findById(id);
    }

    public List<Film> getPopular(Integer count) {
        return storage.getPopular(count);
    }
}
