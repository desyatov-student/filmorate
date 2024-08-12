package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class ReviewDbStorage  extends BaseDbStorage<Review> implements ReviewStorage {

    private static final String FIND_ALL_PREFIX_QUERY = """
            WITH all_reviews AS (
                SELECT r.*,
                SUM(CASE WHEN rr.is_like = true THEN 1 WHEN rr.is_like = false THEN -1 ELSE 0 END) useful
                FROM reviews r
                LEFT JOIN review_rates rr ON rr.REVIEW_ID = r.ID
                GROUP BY r.ID
                ORDER BY useful DESC
            )
            """;
    private static final String FIND_ALL_WITH_LIMIT_QUERY = FIND_ALL_PREFIX_QUERY + """
            SELECT * FROM all_reviews LIMIT ?
            """;
    private static final String FIND_ALL_FOR_FILM_WITH_LIMIT_QUERY = FIND_ALL_PREFIX_QUERY + """
            SELECT * FROM all_reviews WHERE film_id = ? LIMIT ?;
            """;
    private static final String FIND_BY_ID_QUERY = FIND_ALL_PREFIX_QUERY + """
            SELECT * FROM all_reviews WHERE id = ?;
            """;
    private static final String INSERT_QUERY = """
            INSERT INTO reviews (content, is_positive, user_id, film_id)
            VALUES (?, ?, ?, ?);
            """;
    private static final String UPDATE_QUERY = """
            UPDATE reviews SET content = ?, is_positive = ? WHERE id = ?;
            """;
    private static final String DELETE_REVIEW_QUERY = "DELETE reviews WHERE id = ?;";

    // ReviewRate
    private static final String FIND_REVIEW_RATE_QUERY = """
            SELECT * FROM review_rates WHERE review_id = ? AND user_id = ?;
            """;
    private static final String INSERT_REVIEW_RATE_QUERY = """
            INSERT INTO review_rates (review_id, user_id, is_like)
            VALUES (?, ?, ?);
            """;
    private static final String DELETE_REVIEW_RATE_QUERY = "DELETE review_rates WHERE review_id = ? AND user_id = ?;";

    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Review> findAll(Integer count) {
        return findMany(FIND_ALL_WITH_LIMIT_QUERY, count);
    }

    @Override
    public List<Review> findAll(Film film, Integer count) {
        return findMany(FIND_ALL_FOR_FILM_WITH_LIMIT_QUERY, film.getId(), count);
    }

    @Override
    public Optional<Review> findById(Long reviewId) {
        return findOne(FIND_BY_ID_QUERY, reviewId);
    }

    @Override
    public Review create(Review review) {
        long id = insert(
                INSERT_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId()
        );
        review.setId(id);
        review.setUseful(0);
        return review;
    }

    @Override
    public Review update(Review review) {
        update(
                UPDATE_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getId()
        );
        return review;
    }

    @Override
    public void remove(Review review) {
        delete(DELETE_REVIEW_QUERY, review.getId());
    }
}
