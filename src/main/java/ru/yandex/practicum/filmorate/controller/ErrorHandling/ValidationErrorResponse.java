package ru.yandex.practicum.filmorate.controller.ErrorHandling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class ValidationErrorResponse {
    private final List<Violation> violations = new ArrayList<>();
}
