package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;

@Mapper(uses = { MpaMapper.class, GenreMapper.class })
public interface FilmMapper {

    FilmDto toDto(Film film);

    @Mapping(target = "id", ignore = true)
    Film toFilm(NewFilmRequest request);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Film updateFilm(@MappingTarget Film film, UpdateFilmRequest request);
}