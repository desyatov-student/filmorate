package ru.yandex.practicum.filmorate.helpers;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

public class TestData {
    public static User createUser() {
        return new User(
                null,
                "email@mail.ru",
                "login", "name",
                LocalDate.of(1990, 1, 1)
        );
    }

    public static Film createFilm() {
        return new Film(
                null,
                "name",
                "description",
                LocalDate.of(1990, 1, 1), 120,
                List.of(),
                new Mpa(1L, "r17")
        );
    }

    public static Review createReview() {
        return new Review(null, "content", true, null, null, 0);
    }
}
