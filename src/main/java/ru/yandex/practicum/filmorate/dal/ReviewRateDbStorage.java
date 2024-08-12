package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.ReviewRate;
import ru.yandex.practicum.filmorate.storage.ReviewRateStorage;

import java.util.Optional;

@Repository
public class ReviewRateDbStorage extends BaseDbStorage<ReviewRate> implements ReviewRateStorage {

    // ReviewRate
    private static final String FIND_REVIEW_RATE_QUERY = """
            SELECT * FROM review_rates WHERE review_id = ? AND user_id = ?;
            """;
    private static final String INSERT_REVIEW_RATE_QUERY = """
            INSERT INTO review_rates (review_id, user_id, is_like)
            VALUES (?, ?, ?);
            """;
    private static final String UPDATE_REVIEW_RATE_QUERY = """
            UPDATE review_rates SET is_like = ? WHERE id = ?;
            """;
    private static final String DELETE_REVIEW_RATE_QUERY = "DELETE review_rates WHERE id = ?;";

    public ReviewRateDbStorage(JdbcTemplate jdbc, RowMapper<ReviewRate> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<ReviewRate> findReviewRate(Long reviewId, Long userId) {
        return findOne(FIND_REVIEW_RATE_QUERY, reviewId, userId);
    }

    @Override
    public ReviewRate createRate(Long reviewId, Long userId, Boolean isLike) {
        return createReviewRate(reviewId, userId, isLike);
    }

    @Override
    public ReviewRate updateRate(ReviewRate reviewRate) {
        update(
                UPDATE_REVIEW_RATE_QUERY,
                reviewRate.getIsLike(),
                reviewRate.getId()
        );
        return reviewRate;
    }

    @Override
    public void removeRate(ReviewRate reviewRate) {
        delete(DELETE_REVIEW_RATE_QUERY, reviewRate.getId());
    }

    private ReviewRate createReviewRate(Long reviewId, Long userId, boolean isLike) {
        long id = insert(
                INSERT_REVIEW_RATE_QUERY,
                reviewId,
                userId,
                isLike
        );
        return new ReviewRate(id, reviewId, userId, isLike);
    }
}
