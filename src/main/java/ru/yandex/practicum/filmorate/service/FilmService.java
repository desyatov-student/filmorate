package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.mappers.FilmMapperImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmDbStorage;
    private final UserService userService;
    private final FilmMapper filmMapper = new FilmMapperImpl();

    public List<FilmDto> getFilms() {
        return filmDbStorage.findAll().stream()
                .map(filmMapper::toFilmDto)
                .toList();
    }

    public FilmDto getById(Long filmId) {
        Film film = getFilmById(filmId);
        return filmMapper.toFilmDto(film);
    }

    public FilmDto create(NewFilmRequest request) {
        Film film = filmMapper.toFilm(request);
        film = filmDbStorage.save(film);
        log.info("Creating film is successful: {}", film);
        return filmMapper.toFilmDto(film);
    }

    public FilmDto update(Long filmId, UpdateFilmRequest request) {
        if (request.getGenres() != null && request.getGenres().isEmpty()) {
            String message = String.format("Updating film is failed. Genres must be provided. %s", request);
            log.error(message);
            throw new ConditionsNotMetException(message);
        }
        Film film = getFilmById(filmId);
        Film updatedFilm = filmMapper.updateFilm(film, request);
        updatedFilm = filmDbStorage.update(updatedFilm);
        log.info("Updating film is successful: {}", updatedFilm);
        return filmMapper.toFilmDto(updatedFilm);
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
    }

    public void removeLike(Long id, Long userId) {
        UserDto user = getUserById(userId);
        Film film = getFilmById(id);
        filmDbStorage.removeLike(film, user.getId());
    }

    public List<FilmDto> getPopular(Integer count) {
        return filmDbStorage.getPopular(count).stream()
                .map(filmMapper::toFilmDto)
                .toList();
    }

    private Film getFilmById(Long id) {
        return filmDbStorage.findFilmById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с id = %d не найден", id)));
    }

    private UserDto getUserById(Long id) {
        return userService.getById(id);
    }
}
