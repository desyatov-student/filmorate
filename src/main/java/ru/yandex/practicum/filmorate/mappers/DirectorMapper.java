package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.*;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface DirectorMapper {
    DirectorDto toDto(Director director);

    @Mapping(target = "id", ignore = true)
    Director toDirector(NewDirectorRequest request);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Director updateDirector(@MappingTarget Director director, UpdateDirectorRequest request);
}
