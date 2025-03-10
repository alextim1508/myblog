package com.alextim.myblog.repository;

import com.alextim.myblog.model.Comment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

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
        String sql =
                "INSERT INTO " + getTableName() + " " +
                "(content, post_id) " +
                "VALUES (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, comment.getContent());
            pst.setLong(2, comment.getPostId());
            return pst;
        }, keyHolder);

        Long newId;
        if (keyHolder.getKeys().size() > 1) {
            newId = (Long)keyHolder.getKeys().get("id");
        } else {
            newId= keyHolder.getKey().longValue();
        }

        comment.setId(newId);

        return comment;
    }

    @Override
    public int update(Comment comment) {
        String sql =
                "UPDATE " + getTableName() + " " +
                "SET content = ? " +
                "WHERE id = ?";

        return jdbcTemplate.update(sql,
                comment.getContent(),
                comment.getId());
    }

    @Override
    public int countByPostId(Long postId) {
        String sql =
                "SELECT COUNT(*) FROM " + getTableName() + " " +
                "WHERE post_id = ?";

        return jdbcTemplate.queryForObject(sql, Integer.class, postId);
    }

    @Override
    public void deleteByPostId(long id) {
        String sql =
                "DELETE FROM " + getTableName() + " " +
                "WHERE post_id = ?";

        jdbcTemplate.update(sql, id);
    }
}
