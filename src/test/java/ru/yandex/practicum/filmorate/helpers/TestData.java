package ru.yandex.practicum.filmorate.helpers;

import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
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

    public static List<ReviewDto> createReviews() {
        return List.of(
                new ReviewDto(1L, "content1", true, 1L, 1L, 1),
                new ReviewDto(2L, "content2", true, 1L, 1L, 1)
        );
    }
}
