package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.PositiveAndNotNull;
import ru.yandex.practicum.filmorate.validation.ValidDescriptionAndNotBlank;
import ru.yandex.practicum.filmorate.validation.ValidReleaseDateAndNotNull;

import java.time.LocalDate;
import java.util.LinkedHashSet;

@Data
@AllArgsConstructor
public class NewFilmRequest {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Не должно быть пустым")
    private String name;

    @ValidDescriptionAndNotBlank
    private String description;

    @ValidReleaseDateAndNotNull
    private LocalDate releaseDate;

    @PositiveAndNotNull
    private Integer duration;

    private LinkedHashSet<GenreDto> genres;

    @NotNull(message = "Обязательное поле")
    private MpaDto mpa;
}
