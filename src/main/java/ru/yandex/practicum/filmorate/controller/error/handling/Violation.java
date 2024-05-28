package ru.yandex.practicum.filmorate.controller.error.handling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Violation {
    private final String fieldName;
    private final String message;
}
