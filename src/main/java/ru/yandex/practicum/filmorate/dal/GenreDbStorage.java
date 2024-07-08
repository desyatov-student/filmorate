package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage extends BaseDbStorage<Genre> implements GenreStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM genres;";
    private static final String FIND_GENRE_QUERY = "SELECT * FROM genres WHERE id = ?;";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Genre> findById(Long genreId) {
        return findOne(FIND_GENRE_QUERY, genreId);
    }
}
