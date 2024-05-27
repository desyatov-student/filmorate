package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.FilmReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
@AllArgsConstructor
public class Film {
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
}
