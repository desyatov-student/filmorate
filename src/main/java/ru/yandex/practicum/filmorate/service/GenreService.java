package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreDbStorage;
    private final GenreMapper genreMapper;

    public List<GenreDto> getGenres() {
        return genreDbStorage.findAll().stream()
                .map(genreMapper::map)
                .toList();
    }

    public GenreDto getById(Long genreId) {
        Genre genre = genreDbStorage.findById(genreId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Жанр с id = %d не найден", genreId);
                    log.error(errorMessage);
                    return new NotFoundException(errorMessage);
                });
        return genreMapper.map(genre);
    }
}
