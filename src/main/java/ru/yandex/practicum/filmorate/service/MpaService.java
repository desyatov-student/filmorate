package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.MpaMapper;
import ru.yandex.practicum.filmorate.mappers.MpaMapperImpl;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaDbStorage;
    private final MpaMapper mpaMapper = new MpaMapperImpl();

    public List<MpaDto> getMpa() {
        return mpaDbStorage.findAll().stream()
                .map(mpaMapper::map)
                .toList();
    }

    public MpaDto getById(Long genreId) {
        Mpa mpa = mpaDbStorage.findById(genreId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Рейтин с id = %d не найден", genreId);
                    log.error(errorMessage);
                    return new NotFoundException(errorMessage);
                });
        return mpaMapper.map(mpa);
    }
}

