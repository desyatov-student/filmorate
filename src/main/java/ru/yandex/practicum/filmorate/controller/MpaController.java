package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<MpaDto> getGenres() {
        return mpaService.getMpa();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{mpaId}")
    public MpaDto getGenreById(@PathVariable Long mpaId) {
        return mpaService.getById(mpaId);
    }
}