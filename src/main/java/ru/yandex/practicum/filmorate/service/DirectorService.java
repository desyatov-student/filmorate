package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.DirectorMapper;
import ru.yandex.practicum.filmorate.mappers.DirectorMapperImpl;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorDbStorage;
    private final DirectorMapper directorMapper = new DirectorMapperImpl();

    private Director getDirectorById(Long directorId) {
        return getDirectorById(directorId, String.format("Director with id %d is not found", directorId));
    }

    private Director getDirectorById(Long directorId, String errorMessage) {
        return directorDbStorage.findById(directorId)
                .orElseThrow(() -> {
                    log.error(errorMessage);
                    return new NotFoundException(errorMessage);
                });
    }

    public List<DirectorDto> getDirectors() {
        return directorDbStorage.findAll().stream()
                .map(directorMapper::toDto)
                .toList();
    }

    public DirectorDto getById(Long directorId) {
        Director director = getDirectorById(directorId);
        return directorMapper.toDto(director);
    }

    public DirectorDto create(NewDirectorRequest request) {
        Director director = directorMapper.toDirector(request);
        director = directorDbStorage.save(director);
        log.info("Creating director is successful: {}", director);
        return directorMapper.toDto(director);
    }

    public DirectorDto update(UpdateDirectorRequest request) {
        Long directorId = request.getId();
        if (directorId == null) {
            log.error("Updating director is failed: director id is null");
            throw new ConditionsNotMetException("Id must be provided.");
        }

        Director updatedDirector = getDirectorById(directorId);
        directorMapper.updateDirector(updatedDirector, request);
        updatedDirector = directorDbStorage.update(updatedDirector);
        log.info("Updating director is successful: {}", updatedDirector);
        return directorMapper.toDto(updatedDirector);
    }

    public void remove(Long directorId) {
        directorDbStorage.deleteById(directorId);
    }
}