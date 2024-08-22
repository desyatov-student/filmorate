package ru.yandex.practicum.filmorate.dto.director;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateDirectorRequest {
    private Long id;

    @NotBlank
    private String name;
}