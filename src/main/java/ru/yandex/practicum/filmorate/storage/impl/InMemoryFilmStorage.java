package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.IdentifierGenerator;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final IdentifierGenerator identifierGenerator = new IdentifierGenerator();

    public List<Film> findAll() {
        return films.values().stream()
                .sorted(Comparator.comparing(Film::getId))
                .toList();
    }

    @Override
    public Optional<Film> findFilmById(Long filmId) {
        if (!films.containsKey(filmId)) {
            return Optional.empty();
        }
        return Optional.ofNullable(films.get(filmId));
    }

    public Film save(Film film) {
        film.setId(identifierGenerator.getNextId());
        films.put(film.getId(), film);
        return film;
    }

    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void like(Film film, Long userId) {
        film.getLikes().add(userId);
    }

    @Override
    public boolean hasLike(Film film, Long userId) {
        return film.getLikes().contains(userId);
    }

    @Override
    public void removeLike(Film film, Long userId) {
        film.getLikes().remove(userId);
    }

    @Override
    public List<Film> getPopular(Integer count) {
        int maxSize = (count >= films.size()) ? films.size() : count;
        return films.values().stream()
                .sorted(new FilmLikeCountComparator().reversed())
                .limit(maxSize)
                .toList();
    }
}

