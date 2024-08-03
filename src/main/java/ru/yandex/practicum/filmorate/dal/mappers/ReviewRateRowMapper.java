package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.ReviewRate;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewRateRowMapper implements RowMapper<ReviewRate> {
    @Override
    public ReviewRate mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        ReviewRate reviewRate = new ReviewRate();
        reviewRate.setId(resultSet.getLong("id"));
        reviewRate.setIsLike(resultSet.getBoolean("is_like"));
        reviewRate.setReviewId(resultSet.getLong("review_id"));
        reviewRate.setUserId(resultSet.getLong("user_id"));
        return reviewRate;
    }
}
