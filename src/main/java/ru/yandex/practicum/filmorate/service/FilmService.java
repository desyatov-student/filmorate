package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.mappers.FilmMapperImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.ArrayList;
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
        validateGenres(request.getGenres());
        validateMpa(request.getMpa());
        Film film = filmMapper.toFilm(request);
        removeGenreDuplicates(film);
        film = filmDbStorage.save(film);
        log.info("Creating film is successful: {}", film);
        return filmMapper.toFilmDto(getFilmById(film.getId()));
    }

    public FilmDto update(Long filmId, UpdateFilmRequest request) {
        validateGenres(request.getGenres());
        if (request.getMpa() != null) {
            checkMpaForExisting(request.getMpa());
        }
        Film film = getFilmById(filmId);
        film = filmMapper.updateFilm(film, request);
        removeGenreDuplicates(film);
        filmDbStorage.update(film);
        log.info("Updating film is successful: {}", film);
        return filmMapper.toFilmDto(getFilmById(filmId));
    }

    private void removeGenreDuplicates(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        LinkedHashSet<Genre> set = new LinkedHashSet<>(film.getGenres());
        ArrayList<Genre> arrayList = new ArrayList<>(set);
        film.setGenres(arrayList);
    }

    private void validateGenres(List<GenreDto> genres) {
        if (genres != null && !genres.isEmpty()) {
            checkGenresForExisting(genres);
        }
    }

    private void checkGenresForExisting(List<GenreDto> genres) {
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
