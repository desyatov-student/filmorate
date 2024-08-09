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
    private static final String FIND_ALL_QUERY = "SELECT * FROM directors;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE id = ?;";

    private static final String INSERT_QUERY = "INSERT INTO directors (name) VALUES (?);";
    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE id = ?;";

    private static final String DELETE_QUERY = "DELETE FROM directors WHERE id = ?;";

    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Director> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Director> findById(Long directorId) {
        return findOne(FIND_BY_ID_QUERY, directorId);
    }

    @Override
    public Director save(Director director) {
        long id = insert(INSERT_QUERY, director.getName());
        director.setId(id);
        return director;
    }

    @Override
    public Director update(Director newDirector) {
        update(UPDATE_QUERY, newDirector.getName(), newDirector.getId());
        return newDirector;
    }

    @Override
    public boolean deleteById(Long directorId) {
        return delete(DELETE_QUERY, directorId);
    }
}