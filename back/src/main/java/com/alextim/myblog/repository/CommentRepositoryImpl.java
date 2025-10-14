package com.alextim.myblog.repository;

import com.alextim.myblog.model.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Slf4j
@Repository
public class CommentRepositoryImpl extends AbstractRepositoryImpl<Comment> implements CommentRepository {

    public CommentRepositoryImpl(JdbcTemplate jdbcTemplate,
                                 RowMapper<Comment> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    @Override
    public String getTableName() {
        return "comment";
    }

    @Override
    public Comment save(Comment comment) {
        String sql = """
                INSERT INTO %s (content, post_id) VALUES (?, ?)
                """.formatted(getTableName());

        log.info("Executing SQL: {}", sql);
        log.info("Saving comment with text: {} for postId: {}", comment.getText(), comment.getPostId());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, comment.getText());
            pst.setLong(2, comment.getPostId());
            return pst;
        }, keyHolder);

        Long newId;
        if (keyHolder.getKeys().size() > 1) {
            newId = (Long) keyHolder.getKeys().get("id");
        } else {
            newId = keyHolder.getKey().longValue();
        }

        comment.setId(newId);
        log.info("Saved comment with ID: {}", newId);

        return comment;
    }

    @Override
    public int update(Comment comment) {
        String sql = """
                UPDATE %s SET content = ? WHERE id = ?
                """.formatted(getTableName());

        log.info("Executing SQL: {}", sql);
        log.info("Updating comment with ID: {} and text: {}", comment.getId(), comment.getText());

        int rowsAffected = jdbcTemplate.update(sql,
                comment.getText(),
                comment.getId());

        log.info("Updated {} row(s)", rowsAffected);

        return rowsAffected;
    }

    @Override
    public List<Comment> findByPostId(long postId) {
        String sql = """
                SELECT * FROM %s WHERE post_id = ?
                """.formatted(getTableName());

        log.info("Executing SQL: {}", sql);
        log.info("Finding comments for postId: {}", postId);

        List<Comment> comments = jdbcTemplate.query(sql, rowMapper, postId);

        log.info("Found {} comment(s)", comments.size());

        return comments;
    }

    @Override
    public int countByPostId(long postId) {
        String sql = """
                SELECT COUNT(*) FROM %s WHERE post_id = ?
                """.formatted(getTableName());

        log.info("Executing SQL: {}", sql);
        log.info("Counting comments for postId: {}", postId);

        int count = jdbcTemplate.queryForObject(sql, Integer.class, postId);

        log.info("Counted {} comment(s)", count);

        return count;
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