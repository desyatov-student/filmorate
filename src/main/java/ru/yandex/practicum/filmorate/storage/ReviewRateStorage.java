package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.ReviewRate;

import java.util.Optional;

public interface ReviewRateStorage {

    Optional<ReviewRate> findReviewRate(Long reviewId, Long userId);

    ReviewRate createRate(Long reviewId, Long userId, Boolean isLike);

    ReviewRate updateRate(ReviewRate reviewRate);

    void removeRate(ReviewRate reviewRate);
}