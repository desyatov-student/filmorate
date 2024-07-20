package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.validation.NotBlankOrNull;
import ru.yandex.practicum.filmorate.validation.PositiveOrNull;
import ru.yandex.practicum.filmorate.validation.ValidDescriptionOrNull;
import ru.yandex.practicum.filmorate.validation.ValidReleaseDateOrNull;

import java.time.LocalDate;
import java.util.LinkedHashSet;

@Data
public class UpdateFilmRequest {
    private Long id;

    @NotBlankOrNull
    private String name;

    @ValidDescriptionOrNull
    private String description;

    @ValidReleaseDateOrNull
    private LocalDate releaseDate;

    @PositiveOrNull
    private Integer duration;

    private LinkedHashSet<GenreDto> genres;

    private MpaDto mpa;
}
