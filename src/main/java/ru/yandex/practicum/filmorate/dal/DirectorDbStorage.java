package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("directorDbStorage")
public class DirectorDbStorage extends BaseDbStorage<Director> implements DirectorStorage {

    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Director> findAll() {
        return List.of();
    }

    @Override
    public Optional<Director> findById(Long directorId) {
        return Optional.empty();
    }

    @Override
    public Director save(Director director) {
        return null;
    }

    @Override
    public Director update(Director newDirector) {
        return null;
    }

    @Override
    public boolean deleteById(Long directorId) {
        return false;
    }
}