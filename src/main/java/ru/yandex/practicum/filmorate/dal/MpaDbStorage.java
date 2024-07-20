package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage extends BaseDbStorage<Mpa> implements MpaStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa;";
    private static final String FIND_MPA_QUERY = "SELECT * FROM mpa WHERE id = ?;";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Mpa> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Mpa> findById(Long genreId) {
        return findOne(FIND_MPA_QUERY, genreId);
    }
}