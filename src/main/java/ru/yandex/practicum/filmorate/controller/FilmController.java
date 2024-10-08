package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.SearchMode;
import ru.yandex.practicum.filmorate.model.SortOrderFilmsByDirector;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<FilmDto> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{filmId}")
    public FilmDto findById(@PathVariable Long filmId) {
        return filmService.getById(filmId);
    }

    @PostMapping
    public FilmDto create(@Valid @RequestBody NewFilmRequest request) {
        return filmService.create(request);
    }

    @PutMapping("/{filmId}")
    public FilmDto update(@PathVariable Long filmId, @Valid @RequestBody UpdateFilmRequest request) {
        return filmService.update(filmId, request);
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody UpdateFilmRequest request) {
        return filmService.update(request.getId(), request);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        filmService.like(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        filmService.removeLike(id, userId);
    }

    @DeleteMapping("/{filmId}")
    public void removeFilm(@PathVariable Long filmId) {
        filmService.removeFilm(filmId);
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopular(@RequestParam(required = false) Long count,
                                    @RequestParam(required = false) Long genreId,
                                    @RequestParam(required = false) Long year) {
        return filmService.getPopular(count, genreId, year);
    }

    @GetMapping("/common")
    public List<FilmDto> getCommon(@RequestParam Long userId, @RequestParam Long friendId) {
        return filmService.getCommon(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDto> getDirectorFilms(
            @PathVariable Long directorId,
            @RequestParam(defaultValue = "year") String sortBy
    ) {
        return filmService.getDirectorFilms(directorId, SortOrderFilmsByDirector.from(sortBy));
    }

    @GetMapping("/search")
    public List<FilmDto> search(
            @RequestParam String query,
            @RequestParam List<String> by
    ) {
        return filmService.search(
                query,
                by.stream().map(SearchMode::from).toList()
        );
    }
}