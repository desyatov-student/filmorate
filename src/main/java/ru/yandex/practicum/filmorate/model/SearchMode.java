package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.exception.ValidationException;

public enum SearchMode {
    DIRECTOR,
    TITLE;

    public static SearchMode from(String value) {
        try {
            return SearchMode.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            String message = String.format("Unknown search parameter: %s", value.toLowerCase());
            throw new ValidationException(message);
        }
    }
}
