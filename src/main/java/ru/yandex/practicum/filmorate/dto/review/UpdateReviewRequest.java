package ru.yandex.practicum.filmorate.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.NotBlankOrNull;

@Data
@AllArgsConstructor
public class UpdateReviewRequest {
    @NotNull
    @JsonProperty("reviewId")
    Long id;
    @NotBlankOrNull
    private String content;
    private Boolean isPositive;
    private Long userId;
    private Long filmId;
}