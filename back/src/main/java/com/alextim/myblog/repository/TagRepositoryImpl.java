package com.alextim.myblog.repository;

import com.alextim.myblog.model.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Slf4j
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

    private final String RELATIONSHIP_TABLE = "post_tag";

    @Override
    public Tag save(Tag tag) {
        String sql = """
                INSERT INTO %s (title) VALUES (?)
                """.formatted(getTableName());

        log.info("Executing SQL: {}", sql);
        log.info("Saving tag with title: {}", tag.getTitle());

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
            newId = keyHolder.getKey().longValue();
        }

        tag.setId(newId);
        log.info("Saved tag with ID: {}", newId);

        return tag;
    }

    @Override
    public void saveRelationship(long tagId, long postId) {
        String sql = """
                INSERT INTO %s (post_id, tag_id) VALUES (?, ?)
                """.formatted(RELATIONSHIP_TABLE);

        log.info("Executing SQL: {}", sql);
        log.info("Saving relationship: tagId={}, postId={}", tagId, postId);

        jdbcTemplate.update(sql, postId, tagId);
        log.info("Relationship saved successfully.");
    }

    @Override
    public List<Tag> findTagsByPostId(long postId) {
        String sql = """
                SELECT t.id, t.title FROM %s t
                JOIN %s pt ON t.id = pt.tag_id
                WHERE pt.post_id = ?
                """.formatted(getTableName(), RELATIONSHIP_TABLE);

        log.info("Executing SQL: {}", sql);
        log.info("Finding tags for postId: {}", postId);

        List<Tag> tags = jdbcTemplate.query(sql, rowMapper, postId);
        log.info("Found tags: {}", tags);

        return tags;
    }

    @Override
    public Optional<Tag> findTagByTitle(String title) {
        String sql = """
                SELECT * FROM %s WHERE title = ?
                """.formatted(getTableName());

        log.info("Executing SQL: {}", sql);
        log.info("Finding tag by title: {}", title);

        try {
            Tag tag = jdbcTemplate.queryForObject(sql, rowMapper, title);
            log.info("Found tag: {}", tag);
            return Optional.ofNullable(tag);
        } catch (EmptyResultDataAccessException e) {
            log.info("No tag found with title: {}", title);
            return Optional.empty();
        }
    }

    @Override
    public List<Tag> findTagsByTitleIn(List<String> titles) {
        if (titles.isEmpty()) {
            log.info("No titles provided, returning empty list.");
            return Collections.emptyList();
        }

        String inSql = String.join(",", Collections.nCopies(titles.size(), "?"));
        String sql = """
                SELECT * FROM %s WHERE title IN (%s)
                """.formatted(getTableName(), inSql);

        log.info("Executing SQL: {}", sql);
        log.info("Finding tags by titles: {}", titles);

        List<Tag> tags = jdbcTemplate.query(sql, rowMapper, titles.toArray());
        log.info("Found tags: {}", tags);

        return tags;
    }

    @Override
    public void deleteRelationshipByPostId(long postId) {
        String sql = """
                DELETE FROM %s WHERE post_id = ?
                """.formatted(RELATIONSHIP_TABLE);

        log.info("Executing SQL: {}", sql);
        log.info("Deleting relationships for postId: {}", postId);

        int rowsAffected = jdbcTemplate.update(sql, postId);
        log.info("Deleted {} relationship(s) for postId: {}", rowsAffected, postId);
    }

    @Override
    public void deleteRelationships() {
        String sql = """
                DELETE FROM %s
                """.formatted(RELATIONSHIP_TABLE);

        log.info("Executing SQL: {}", sql);
        log.info("Deleting all relationships");

        int rowsAffected = jdbcTemplate.update(sql);
        log.info("Deleted {} relationship(s)", rowsAffected);
    }
}