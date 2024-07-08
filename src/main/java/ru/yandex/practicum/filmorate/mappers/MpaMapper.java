package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;

@Mapper
public interface MpaMapper {
    MpaDto map(Mpa mpa);
}
