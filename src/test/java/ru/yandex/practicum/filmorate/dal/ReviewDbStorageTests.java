package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.helpers.TestData;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewRate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan
public class ReviewDbStorageTests {

    private final ReviewDbStorage reviewStorage;
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    @Test
    public void testFindById_returnReview_ReviewWasSaved() {

        // Given
        Review initialReview = createReview();

        // When
        Optional<Review> reviewOptional = reviewStorage.findById(initialReview.getId());

        // Then
        assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(review -> {
                    assertThat(review).isEqualTo(initialReview);
                });
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testLikeReview_returnReviewRateAndLikeTrueOrFalse_ReviewRateWasSaved(boolean isLike) {

        // Given
        Review review = createReview();
        ReviewRate reviewRateExpected = createReviewRate(review, isLike);

        // When
        Optional<ReviewRate> reviewRateOptional = reviewStorage.findReviewRate(review, reviewRateExpected.getUserId());

        // Then
        assertThat(reviewRateOptional)
                .isPresent()
                .hasValueSatisfying(reviewRate -> {
                    assertThat(reviewRate).isEqualTo(reviewRateExpected);
                    assertThat(reviewRate.getIsLike()).isEqualTo(isLike);
                    assertThat(review.getUserId()).isNotEqualTo(reviewRate.getUserId());
                });
    }

    @Test
    public void testLikeReview_returnReviewsOrderedByUseful_Review2Like3TimesAndDislike1TimeAndReview1Like1Time() {

        // Given
        Review review1 = createReview();
        Review review2 = createReview();
        createReviewRate(review1, true);

        createReviewRate(review2, true);
        createReviewRate(review2, true);
        createReviewRate(review2, true);
        createReviewRate(review2, false);

        // When
        List<Review> reviews = reviewStorage.findAll(10);

        // Then
        assertThat(reviews.get(0).getUseful()).isEqualTo(2);
        assertThat(reviews.get(0).getId()).isEqualTo(review2.getId());

        assertThat(reviews.get(1).getUseful()).isEqualTo(1);
        assertThat(reviews.get(1).getId()).isEqualTo(review1.getId());
    }

    private Review createReview() {
        Long userId = userStorage.save(TestData.createUser()).getId();
        Long filmId = filmStorage.save(TestData.createFilm()).getId();

        Review review = TestData.createReview();
        review.setUserId(userId);
        review.setFilmId(filmId);
        Long reviewId = reviewStorage.save(review).getId();
        review.setId(reviewId);
        return review;
    }

    private ReviewRate createReviewRate(Review review, boolean isLike) {
        Long userId = userStorage.save(TestData.createUser()).getId();
        ReviewRate reviewRate = new ReviewRate(null, review.getId(), userId, isLike);
        Long reviewRateId;
        if (isLike) {
            reviewRateId = reviewStorage.createLike(review, userId).getId();
        } else {
            reviewRateId = reviewStorage.createDislike(review, userId).getId();
        }
        reviewRate.setId(reviewRateId);
        return reviewRate;
    }
}
