package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewRate;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    List<Review> findAll(Integer count);

    List<Review> findAll(Film film, Integer count);

    Optional<Review> findById(Long reviewId);

    Review save(Review review);

    Review update(Review review);

    void remove(Review review);

    Optional<ReviewRate> findReviewRate(Review review, Long userId);

    ReviewRate createLike(Review review, Long userId);

    ReviewRate createDislike(Review review, Long userId);

    boolean removeRate(Review review, Long userId);
}