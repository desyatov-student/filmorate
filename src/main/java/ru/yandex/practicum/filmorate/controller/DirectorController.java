package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@RequestMapping("/directors")
@Slf4j
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public List<DirectorDto> getDirectors() {
        return directorService.getDirectors();
    }

    @GetMapping("/{id}")
    public DirectorDto getDirectorById(@PathVariable("id") Long directorId) {
        return directorService.getById(directorId);
    }

    @PostMapping
    public DirectorDto create(@Valid @RequestBody NewDirectorRequest newDirectorRequest) {
        return directorService.create(newDirectorRequest);
    }

    @PutMapping
    public DirectorDto update(@Valid @RequestBody UpdateDirectorRequest updateDirectorRequest) {
        return directorService.update(updateDirectorRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteLike(@PathVariable("id") long directorId) {
        directorService.remove(directorId);
    }
}