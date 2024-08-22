package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<ReviewDto> getReviews(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") Integer count
    ) {
        if (filmId == null) {
            return reviewService.getReviews(count);
        }
        return reviewService.getReviews(filmId, count);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ReviewDto getById(@PathVariable Long id) {
        return reviewService.getById(id);
    }

    @PostMapping
    public ReviewDto create(@Valid @RequestBody NewReviewRequest request) {
        return reviewService.create(request);
    }

    @PutMapping
    public ReviewDto update(@Valid @RequestBody UpdateReviewRequest request) {
        return reviewService.update(request);
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable Long id) {
        reviewService.removeReview(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public ReviewDto like(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.like(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ReviewDto dislike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.dislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ReviewDto removeLike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.removeRate(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ReviewDto removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.removeRate(id, userId);
    }
}