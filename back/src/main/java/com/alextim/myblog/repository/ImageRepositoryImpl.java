package com.alextim.myblog.repository;

import com.alextim.myblog.model.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Slf4j
@Repository
public class ImageRepositoryImpl extends AbstractRepositoryImpl<Image> implements ImageRepository {

    public ImageRepositoryImpl(JdbcTemplate jdbcTemplate, RowMapper<Image> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    @Override
    public String getTableName() {
        return "image";
    }

    @Override
    public Image save(Image image) {
        String sql = """
                INSERT INTO %s (data, file_name, size, post_id) VALUES (?, ?, ?, ?)
                """.formatted(getTableName());

        log.info("Executing SQL: {}", sql);
        log.info("Saving image for postId: {} with filename: {} and size: {}", image.getPostId(), image.getFileName(), image.getSize());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setBytes(1, image.getData());
            pst.setString(2, image.getFileName());
            pst.setLong(3, image.getSize());
            pst.setLong(4, image.getPostId());
            return pst;
        }, keyHolder);

        Long newId;
        if (keyHolder.getKeys().size() > 1) {
            newId = (Long) keyHolder.getKeys().get("id");
        } else {
            newId = keyHolder.getKey().longValue();
        }

        image.setId(newId);
        log.info("Saved image with ID: {}", newId);

        return image;
    }

    @Override
    public int update(Image image) {
        String sql = """
                UPDATE %s SET data = ?, file_name = ?, size = ?, post_id = ? WHERE id = ?
                """.formatted(getTableName());

        log.info("Executing SQL: {}", sql);
        log.info("Updating image with ID: {}, filename: {}, size: {}, postId: {}",
                image.getId(), image.getFileName(), image.getSize(), image.getPostId());

        int rowsAffected = jdbcTemplate.update(sql,
                image.getData(),
                image.getFileName(),
                image.getSize(),
                image.getPostId(),
                image.getId());

        log.info("Updated {} row(s)", rowsAffected);

        return rowsAffected;
    }

    @Override
    public Optional<Image> findByPostId(long postId) {
        String sql = """
                SELECT * FROM %s WHERE post_id = ?
                """.formatted(getTableName());

        log.info("Executing SQL: {}", sql);
        log.info("Finding image for postId: {}", postId);

        try {
            Image image = jdbcTemplate.queryForObject(sql, rowMapper, postId);
            log.info("Found image: {}", image);
            return Optional.ofNullable(image);
        } catch (EmptyResultDataAccessException e) {
            log.info("No image found for postId: {}", postId);
            return Optional.empty();
        }
    }

    @Override
    public void deleteByPostId(long id) {
        String sql = """
                DELETE FROM %s WHERE post_id = ?
                """.formatted(getTableName());

        log.info("Executing SQL: {}", sql);
        log.info("Deleting comments for postId: {}", id);

        int rowsAffected = jdbcTemplate.update(sql, id);

        log.info("Deleted {} comment(s) for postId: {}", rowsAffected, id);
    }
}