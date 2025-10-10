package com.alextim.myblog.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class AbstractRepositoryImpl<T> implements AbstractRepository<T> {

    protected final JdbcTemplate jdbcTemplate;
    protected final RowMapper<T> rowMapper;

    public abstract String getTableName();

    @Override
    public Optional<T> findById(Long id) {
        String sql =
                "SELECT * FROM " + getTableName() + " " +
                "WHERE id = ?";

        try {
            T result = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<T> findAll() {
        String sql =
                "SELECT * FROM " + getTableName() + " " +
                "ORDER BY id";

        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public List<T> findAll(int limit, int offset) {
        String sql =
                "SELECT * FROM " + getTableName() + " " +
                "ORDER BY id ASC LIMIT ? OFFSET ?";

        return jdbcTemplate.query(sql, rowMapper, limit, offset);
    }

    @Override
    public void deleteById(Long id) {
        String sql =
                "DELETE FROM " + getTableName() + " " +
                "WHERE id = ?";

        jdbcTemplate.update(sql, id);
    }

    @Override
    public void delete() {
        String sql =
                "DELETE FROM " + getTableName();

        jdbcTemplate.update(sql);
    }

    @Override
    public int count() {
        String sql =
                "SELECT COUNT(*) FROM " + getTableName();

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}
