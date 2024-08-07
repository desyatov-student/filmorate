package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.mappers.FilmMapperImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmDbStorage;
    private final GenreStorage genreDbStorage;
    private final MpaStorage mpaDbStorage;
    private final UserService userService;
    private final FeedStorage feedDbStorage;
    private final FilmMapper filmMapper = new FilmMapperImpl();

    public List<FilmDto> getFilms() {
        return filmDbStorage.findAll().stream()
                .map(filmMapper::toDto)
                .toList();
    }

    public FilmDto getById(Long filmId) {
        Film film = getFilmById(filmId);
        return filmMapper.toDto(film);
    }

    public Film getFilmById(Long id) {
        return filmDbStorage.findFilmById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с id = %d не найден", id)));
    }

    public FilmDto create(NewFilmRequest request) {
        validateGenres(request.getGenres());
        validateMpa(request.getMpa());
        Film film = filmMapper.toFilm(request);
        film = filmDbStorage.save(film);
        log.info("Creating film is successful: {}", film);
        return filmMapper.toDto(getFilmById(film.getId()));
    }

    public FilmDto update(Long filmId, UpdateFilmRequest request) {
        validateGenres(request.getGenres());
        if (request.getMpa() != null) {
            checkMpaForExisting(request.getMpa());
        }
        Film film = getFilmById(filmId);
        film = filmMapper.updateFilm(film, request);
        filmDbStorage.update(film);
        log.info("Updating film is successful: {}", film);
        return filmMapper.toDto(getFilmById(filmId));
    }

    private void validateGenres(LinkedHashSet<GenreDto> genres) {
        if (genres != null && !genres.isEmpty()) {
            checkGenresForExisting(genres);
        }
    }

    private void checkGenresForExisting(LinkedHashSet<GenreDto> genres) {
        for (GenreDto genre : genres) {
            if (genreDbStorage.findById(genre.getId()).isEmpty()) {
                throw new ValidationException(String.format("Genre not found id=%s", genre.getId()));
            }
        }
    }

    private void validateMpa(MpaDto mpa) {
        if (mpa == null) {
            String message = "Creating film is failed. Mpa must be provided.";
            log.error(message);
            throw new InternalServerException(message);
        } else {
            checkMpaForExisting(mpa);
        }
    }

    private void checkMpaForExisting(MpaDto mpa) {
        if (mpaDbStorage.findById(mpa.getId()).isEmpty()) {
            throw new ValidationException(String.format("Mpa not found id=%s", mpa.getId()));
        }
    }

    public void like(Long id, Long userId) {
        UserDto user = getUserById(userId);
        Film film = getFilmById(id);
        if (filmDbStorage.hasLike(film, user.getId())) {
            String message = String.format("Like exists for filmId = %d, userId = %d", film.getId(), user.getId());
            log.error(message);
            throw new DuplicatedDataException(message);
        }
        filmDbStorage.like(film, user.getId());
        feedDbStorage.save(
                new FeedDto(
                        Instant.now().toEpochMilli(),
                        userId,
                        "LIKE",
                        "ADD",
                        id));
    }

    public void removeLike(Long id, Long userId) {
        UserDto user = getUserById(userId);
        Film film = getFilmById(id);
        filmDbStorage.removeLike(film, user.getId());
        feedDbStorage.save(
                new FeedDto(
                        Instant.now().toEpochMilli(),
                        userId,
                        "LIKE",
                        "REMOVE",
                        id));
    }

    public void removeFilm(Long id) {
        Film film = getFilmById(id);
        filmDbStorage.removeFilm(film);
    }

    public List<FilmDto> getPopular(Integer count) {
        return filmDbStorage.getPopular(count).stream()
                .map(filmMapper::toDto)
                .toList();
    }

    private UserDto getUserById(Long id) {
        return userService.getById(id);
    }
}
