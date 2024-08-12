package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewRate;
import ru.yandex.practicum.filmorate.storage.ReviewRateStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final ReviewRateStorage reviewRateStorage;
    private final FilmService filmService;
    private final UserService userService;
    private final ReviewMapper reviewMapper;

    public List<ReviewDto> getReviews(Integer count) {
        return reviewStorage.findAll(count).stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    public List<ReviewDto> getReviews(Long filmId, Integer count) {
        Film film = filmService.getFilmById(filmId);
        return reviewStorage.findAll(film, count).stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    public ReviewDto getById(Long reviewId) {
        Review review = getReviewById(reviewId);
        return reviewMapper.toDto(review);
    }

    public Review getReviewById(Long reviewId) {
        return reviewStorage.findById(reviewId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Отзыв с id = %d не найден", reviewId);
                    log.error(errorMessage);
                    return new NotFoundException(errorMessage);
                });
    }

    public ReviewDto create(NewReviewRequest request) {
        userService.getUserById(request.getUserId());
        filmService.getFilmById(request.getFilmId());
        Review review = reviewMapper.toReview(request);
        review = reviewStorage.create(review);
        log.info("Creating review is successful: {}", review);
        return reviewMapper.toDto(review);
    }

    public ReviewDto update(UpdateReviewRequest request) {
        if (request.getUserId() != null) {
            userService.getUserById(request.getUserId());
        }
        if (request.getFilmId() != null) {
            filmService.getFilmById(request.getFilmId());
        }
        Review review = getReviewById(request.getId());
        review = reviewMapper.updateReview(review, request);
        review = reviewStorage.update(review);
        log.info("Updating review is successful: {}", review);
        return reviewMapper.toDto(review);
    }

    public void removeReview(Long reviewId) {
        Review review = getReviewById(reviewId);
        reviewStorage.remove(review);
    }

    public ReviewDto like(Long reviewId, Long userId) {
        Review review = getReview(reviewId, userId, true);
        return reviewMapper.toDto(review);
    }

    public ReviewDto dislike(Long reviewId, Long userId) {
        Review review = getReview(reviewId, userId, false);
        return reviewMapper.toDto(review);
    }

    public ReviewDto removeRate(Long reviewId, Long userId) {
        Review review = getReviewById(reviewId);
        Optional<ReviewRate> reviewRateOpt = reviewRateStorage.findReviewRate(reviewId, userId);
        reviewRateOpt.ifPresentOrElse(reviewRate -> {
            Integer useful = review.getUseful() + (reviewRate.getIsLike() ? -1 : 1);
            review.setUseful(useful);
            reviewRateStorage.removeRate(reviewRate);
        }, () -> {
            String errorMessage = String.format("Rate not found for reviewId = %d and userId = %d", reviewId, userId);
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        });
        return reviewMapper.toDto(review);
    }

    private Review getReview(Long reviewId, Long userId, Boolean targetIsLike) {
        Review review = getReviewById(reviewId);
        Optional<ReviewRate> reviewRateOpt = reviewRateStorage.findReviewRate(reviewId, userId);
        if (reviewRateOpt.isPresent()) {
            ReviewRate reviewRate = reviewRateOpt.get();
            updateReviewUsefulAndRate(review, reviewRate, targetIsLike);
            reviewRateStorage.updateRate(reviewRate);
        } else {
            reviewRateStorage.createRate(reviewId, userId, targetIsLike);
            Integer useful = targetIsLike ? review.getUseful() + 1 : review.getUseful() - 1;
            review.setUseful(useful);
        }
        return review;
    }

    private void updateReviewUsefulAndRate(Review review, ReviewRate reviewRate, Boolean targetIsLike) {
        if (targetIsLike == reviewRate.getIsLike()) {
            return;
        }
        if (reviewRate.getIsLike() && !targetIsLike) {
            review.setUseful(review.getUseful() - 2);
        } else {
            review.setUseful(review.getUseful() + 2);
        }
        reviewRate.setIsLike(targetIsLike);
    }
}