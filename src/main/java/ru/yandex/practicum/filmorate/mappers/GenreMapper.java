package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface GenreMapper {
    GenreDto map(Genre genre);
}
