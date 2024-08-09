package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.mappers.DirectorMapper;
import ru.yandex.practicum.filmorate.mappers.DirectorMapperImpl;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorDbStorage;
    private final DirectorMapper directorMapper = new DirectorMapperImpl();

    public List<DirectorDto> getUsers() {
        return directorDbStorage.findAll().stream()
                .map(directorMapper::toDto)
                .toList();
    }
}