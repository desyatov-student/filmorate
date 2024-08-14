package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<GenreDto> getGenres() {
        return genreService.getGenres();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{genreId}")
    public GenreDto getGenreById(@PathVariable Long genreId) {
        return genreService.getById(genreId);
    }
}
