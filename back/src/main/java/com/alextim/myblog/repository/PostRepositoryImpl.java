package com.alextim.myblog.repository;

import com.alextim.myblog.model.Post;
import com.alextim.myblog.service.CommentService;
import com.alextim.myblog.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

@Slf4j
@Repository
public class PostRepositoryImpl extends AbstractRepositoryImpl<Post> implements PostRepository {

    private final CommentService commentService;
    private final TagService tagService;

    public PostRepositoryImpl(JdbcTemplate jdbcTemplate,
                              RowMapper<Post> postRowMapper,
                              CommentService commentService,
                              TagService tagService) {
        super(jdbcTemplate, postRowMapper);

        this.commentService = commentService;
        this.tagService = tagService;
    }

    @Override
    public String getTableName() {
        return "post";
    }

    private String TAG_TABLE = "tag";
    private String RELATIONSHIP_TABLE = "post_tag";

    @Override
    public Post save(Post post) {
        log.info("Saving post with title: {}", post.getTitle());
        log.debug("Post data: {}", post);

        String sql = """
                INSERT INTO %s (title, content, likeCount) VALUES (?, ?, ?)
                """.formatted(getTableName());
        log.debug("Executing SQL: {}", sql);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, post.getTitle());
            pst.setString(2, post.getText());
            pst.setInt(3, post.getLikesCount());
            return pst;
        }, keyHolder);

        Long newId;
        if (keyHolder.getKeys().size() > 1) {
            newId = (Long) keyHolder.getKeys().get("id");
        } else {
            newId = keyHolder.getKey().longValue();
        }

        post.setId(newId);
        log.info("Saved post with ID: {}", newId);

        return post;
    }

    @Override
    public int update(Post post) {
        log.info("Updating post with ID: {}", post.getId());
        log.info("Post data: {}", post);

        String sql = """
            UPDATE %s
            SET title = ?, content = ?, likeCount = ?
            WHERE id = ?
            """.formatted(getTableName());
        log.info("Executing SQL: {}", sql);

        int rowsAffected = jdbcTemplate.update(sql,
                post.getTitle(),
                post.getText(),
                post.getLikesCount(),
                post.getId());

        log.info("Updated post with ID: {}, rows affected: {}", post.getId(), rowsAffected);
        return rowsAffected;
    }

    @Override
    public List<Post> findByTagIds(List<Long> tagIds, int limit, int offset) {
        log.info("Finding posts by tag IDs: {}, limit: {}, offset: {}", tagIds, limit, offset);

        if (tagIds.isEmpty()) {
            log.warn("Tag IDs list is empty, returning empty list");
            return Collections.emptyList();
        }

        String inSql = String.join(",", Collections.nCopies(tagIds.size(), "?"));

        String sql = """
            SELECT p.id as id,
                   p.title as title,
                   p.content as content,
                   p.likeCount as likeCount
            FROM %s p
            INNER JOIN %s pt ON p.id = pt.post_id
            INNER JOIN %s t ON pt.tag_id = t.id
            WHERE t.id IN (%s)
            GROUP BY p.id
            ORDER BY p.id
            LIMIT ? OFFSET ?
        """.formatted(getTableName(), RELATIONSHIP_TABLE, TAG_TABLE, inSql);
        log.debug("Executing SQL: {}", sql);
        log.info("Executing SQL: {}", sql);

        Object[] params = new Object[tagIds.size() + 2];
        for (int i = 0; i < tagIds.size(); i++) {
            params[i] = tagIds.get(i);
        }
        params[tagIds.size()] = limit;
        params[tagIds.size() + 1] = offset;

        List<Post> posts = jdbcTemplate.query(sql, rowMapper, params);

        log.info("Found {} posts by tag IDs", posts.size());
        log.info("Posts found: {}", posts.stream().map(Post::getTitle).toList());

        return posts;
    }


    @Override
    public List<Post> findByTitleOrContent(String query, int limit, int offset) {
        log.info("Finding posts by title or content with query: '{}', limit: {}, offset: {}", query, limit, offset);

        if (query == null || query.trim().isEmpty()) {
            log.warn("Query is null or empty, returning empty list");
            return Collections.emptyList();
        }

        String sql = """
            SELECT id as id,
                   title as title,
                   content as content,
                   likeCount as likeCount
            FROM %s
            WHERE LOWER(title) LIKE LOWER(?)
               OR LOWER(content) LIKE LOWER(?)
            ORDER BY id
            LIMIT ? OFFSET ?
            """.formatted(getTableName());
        log.debug("Executing SQL: {}", sql);

        String likePattern = "%" + query + "%";
        Object[] params = {likePattern, likePattern, limit, offset};

        List<Post> posts = jdbcTemplate.query(sql, rowMapper, params);

        log.info("Found {} posts matching query: '{}'", posts.size(), query);
        log.debug("Posts found: {}", posts.stream().map(Post::getTitle).toList());

        return posts;
    }


    @Override
    public void deleteById(long id) {
        log.info("Deleting post with ID: {}", id);

        commentService.deleteByPostId(id);
        log.info("Deleted comments for post ID: {}", id);

        tagService.deleteRelationshipByPostId(id);
        log.info("Deleted tag relationships for post ID: {}", id);

        super.deleteById(id);
        log.info("Successfully deleted post with ID: {}", id);
    }

    @Override
    public void delete() {
        log.info("Deleting all posts");
        String sql = "DELETE FROM " + getTableName();
        log.info("Executing SQL: {}", sql);

        int rowsAffected = jdbcTemplate.update(sql);
        log.info("Deleted {} posts", rowsAffected);
    }
}