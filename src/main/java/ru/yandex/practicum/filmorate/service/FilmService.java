package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmDbStorage;
    private final GenreStorage genreDbStorage;
    private final DirectorStorage directorDbStorage;
    private final MpaStorage mpaDbStorage;
    private final UserService userService;
    private final FilmMapper filmMapper;

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
        validateDirectors(request.getDirectors());
        validateMpa(request.getMpa());
        Film film = filmMapper.toFilm(request);
        film = filmDbStorage.save(film);
        log.info("Creating film is successful: {}", film);
        return filmMapper.toDto(getFilmById(film.getId()));
    }

    public FilmDto update(Long filmId, UpdateFilmRequest request) {
        validateGenres(request.getGenres());
        validateDirectors(request.getDirectors());
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
        if (genres != null) {
            genres.stream()
                    .map(GenreDto::getId)
                    .forEach(this::checkGenresForExisting);
        }
    }

    private void checkGenresForExisting(Long genreId) {
        if (genreDbStorage.findById(genreId).isEmpty()) {
            throw new ValidationException(String.format("Genre not found id=%s", genreId));
        }
    }

    private void validateDirectors(LinkedHashSet<DirectorDto> directors) {
        if (directors != null && !directors.isEmpty()) {
            checkDirectorsForExisting(directors);
        }
    }

    private void checkDirectorsForExisting(LinkedHashSet<DirectorDto> directors) {
        for (DirectorDto director : directors) {
            if (directorDbStorage.findById(director.getId()).isEmpty()) {
                throw new ValidationException(String.format("Director not found id=%s", director.getId()));
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
        userService.createFeed(userId, EventType.LIKE, Operation.ADD, id);
        if (filmDbStorage.hasLike(film, user.getId())) {
            return;
        }
        filmDbStorage.like(film, user.getId());
    }

    public void removeLike(Long id, Long userId) {
        UserDto user = getUserById(userId);
        Film film = getFilmById(id);
        filmDbStorage.removeLike(film, user.getId());
        userService.createFeed(userId, EventType.LIKE, Operation.REMOVE, id);
    }

    public void removeFilm(Long id) {
        Film film = getFilmById(id);
        filmDbStorage.removeFilm(film);
    }

    public List<FilmDto> getPopular(Long count, Long genreId, Long year) {
        if (genreId != null) {
            checkGenresForExisting(genreId);
        }
        if (count != null && count < 0) {
            log.error("Find popular films is failed. Count is negative or null");
            throw new ConditionsNotMetException("Count is negative or null");
        }
        if (year != null && year > LocalDate.now().getYear()) {
            log.error("Find popular films is failed. Year is not exist");
            throw new ConditionsNotMetException("Year is not exist");
        }
        return filmDbStorage.getPopular(count, genreId, year).stream()
                .map(filmMapper::toDto)
                .toList();
    }

    public List<FilmDto> search(String query, List<SearchMode> searchMode) {
        if (searchMode.isEmpty()) {
            log.error("Find films is failed. Param \"by\" not valid");
            throw new ConditionsNotMetException("Param \"by\" not valid");
        }
        return filmDbStorage.search(query, searchMode).stream()
                .map(filmMapper::toDto)
                .toList();
    }

    private UserDto getUserById(Long id) {
        return userService.getById(id);
    }

    public List<FilmDto> getCommon(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);
        return filmDbStorage.getCommon(userId, friendId).stream()
                .map(filmMapper::toDto)
                .toList();
    }

    public List<FilmDto> getDirectorFilms(Long directorId, SortOrderFilmsByDirector sortBy) {
        if (sortBy == null) {
            log.error("Find directors films is failed. Param \"sortBy\" not valid");
            throw new ConditionsNotMetException("Param \"sortBy\" not valid");
        }
        if (directorDbStorage.findById(directorId).isEmpty()) {
            throw new NotFoundException(String.format("Director with id = %d not found", directorId));
        }
        return filmDbStorage.getDirectorFilms(directorId, sortBy).stream()
                .map(filmMapper::toDto)
                .toList();
    }
}