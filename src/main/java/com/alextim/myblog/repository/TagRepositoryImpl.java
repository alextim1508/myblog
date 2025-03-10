package com.alextim.myblog.repository;

import com.alextim.myblog.model.Tag;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
public class TagRepositoryImpl extends AbstractRepositoryImpl<Tag> implements TagRepository {

    public TagRepositoryImpl(JdbcTemplate jdbcTemplate,
                             RowMapper<Tag> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    @Override
    public String getTableName() {
        return "tag";
    }

    @Override
    public Tag save(Tag tag) {
        String sql =
                "INSERT INTO " + getTableName() + " " +
                "(title) " +
                "VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, tag.getTitle());
            return pst;
        }, keyHolder);

        Long newId;
        if (keyHolder.getKeys().size() > 1) {
            newId = (Long)keyHolder.getKeys().get("id");
        } else {
            newId= keyHolder.getKey().longValue();
        }

        tag.setId(newId);

        return tag;
    }

    @Override
    public void saveRelationship(Long tagId, Long postId) {
        String sql =
                "INSERT INTO post_tag " +
                "(post_id, tag_id) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sql, postId, tagId);
    }

    @Override
    public Set<Tag> findTagsByPostId(Long postId) {
        String sql =
                "SELECT t.id, t.title FROM tag t " +
                "JOIN post_tag pt ON t.id = pt.tag_id " +
                "WHERE pt.post_id = ?";

        return new HashSet<>(jdbcTemplate.query(sql, rowMapper, postId));
    }

    @Override
    public Optional<Tag> findTagByTitle(String title) {
        String sql =
                "SELECT * FROM tag " +
                "WHERE title = ?";

        return Optional.of(jdbcTemplate.queryForObject(sql, rowMapper, title));
    }

    @Override
    public Set<Tag> findTagsByTitleIn(Set<String> titles) {
        String inSql = String.join(",", Collections.nCopies(titles.size(), "?"));

        String sql =
                "SELECT * FROM tag " +
                "WHERE title IN (" + inSql + ")";

        return new HashSet<>(jdbcTemplate.query(sql, rowMapper, titles.toArray()));
    }

    @Override
    public void deleteRelationshipByTagId(Long id) {
        String sql = "DELETE FROM post_tag WHERE tag_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void deleteRelationships() {
        String sql = "DELETE FROM post_tag";
        jdbcTemplate.update(sql);
    }

}
