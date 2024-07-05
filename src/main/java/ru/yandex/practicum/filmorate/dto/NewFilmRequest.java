package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.FilmReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class NewFilmRequest {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Не должно быть пустым")
    private String name;

    @NotNull(message = "Обязательное поле")
    @Size(max = 200, message = "Должно быть до 200 символов")
    private String description;

    @NotNull(message = "Обязательное поле")
    @FilmReleaseDate
    private LocalDate releaseDate;

    @NotNull(message = "Обязательное поле")
    @Positive(message = "Продолжительность должна быть положительной")
    private Integer duration;

    private final Set<Long> likes = new HashSet<>();

    @NotNull(message = "Обязательное поле")
    private Long genreId;

    @NotNull(message = "Обязательное поле")
    private Long ratingId;
}