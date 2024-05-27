package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.IdentifierGenerator;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
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
    private final IdentifierGenerator identifierGenerator;

    public Collection<Film> findAll() {
        return films.values().stream()
                .sorted(Comparator.comparing(Film::getId))
                .toList();
    }

    @Override
    public Optional<Film> findById(Long filmId) {
        if (!films.containsKey(filmId)) {
            return Optional.empty();
        }
        return Optional.ofNullable(films.get(filmId));
    }

    public Film create(Film film) {
        film.setId(identifierGenerator.getNextId());
        // сохраняем новую публикацию в памяти приложения
        films.put(film.getId(), film);
        log.info("Creating film is successful: {}", film);
        return film;
    }

    public Film update(Film newFilm) {
        // проверяем необходимые условия
        if (newFilm.getId() == null) {
            log.error("Updating film is failed. film id is null {}", newFilm);
            throw new ConditionsNotMetException("Id must be provided.");
        }
        if (!films.containsKey(newFilm.getId())) {
            log.error("Film = {} is not found", newFilm);
            throw new NotFoundException("Film with id = " + newFilm.getId() + " is not found");
        }
        Film oldFilm = films.get(newFilm.getId());
        // если публикация найдена и все условия соблюдены, обновляем её содержимое
        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
        log.info("Updating film is successful: {}", oldFilm);
        return oldFilm;
    }

    @Override
    public void like(Film film, Long userId) {
        film.getLikes().add(userId);
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

