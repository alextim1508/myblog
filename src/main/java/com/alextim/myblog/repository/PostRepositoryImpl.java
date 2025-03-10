package com.alextim.myblog.repository;

import com.alextim.myblog.model.Post;
import com.alextim.myblog.service.CommentService;
import com.alextim.myblog.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class PostRepositoryImpl extends AbstractRepositoryImpl<Post> implements PostRepository {

    private final CommentService commentService;
    private final TagService tagService;

    private final JdbcTemplate jdbcTemplate;
    private final ResultSetExtractor<Post> postWithCommentsRowMapper;
    private final ResultSetExtractor<List<Post>> postWithTagsRowMapper;

    public PostRepositoryImpl(JdbcTemplate jdbcTemplate,
                              RowMapper<Post> postRowMapper,
                              ResultSetExtractor<Post> postWithCommentsRowMapper,
                              ResultSetExtractor<List<Post>> postWithTagsRowMapper,
                              CommentService commentService,
                              TagService tagService) {
        super(jdbcTemplate, postRowMapper);

        this.jdbcTemplate = jdbcTemplate;

        this.postWithCommentsRowMapper = postWithCommentsRowMapper;
        this.postWithTagsRowMapper = postWithTagsRowMapper;

        this.commentService = commentService;
        this.tagService = tagService;
    }

    @Override
    public String getTableName() {
        return "post";
    }

    @Override
    public Post save(Post post) {
        String sql =
                        "INSERT INTO " + getTableName() + " " +
                        "(title, content, imageUrl, likeCount) " +
                        "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, post.getTitle());
            pst.setString(2, post.getContent());
            pst.setString(3, post.getImageUrl());
            pst.setInt(4, post.getLikeCount());
            return pst;
        }, keyHolder);


        Long newId;
        if (keyHolder.getKeys().size() > 1) {
            newId = (Long)keyHolder.getKeys().get("id");
        } else {
            newId= keyHolder.getKey().longValue();
        }

        post.setId(newId);

        post.getTags().forEach(tag -> tagService.save(tag, post.getId()));

        return post;
    }

    @Override
    public int update(Post post) {
        String sql =
                "UPDATE " + getTableName() + " " +
                "SET " +
                    "title = ?, " +
                    "content = ?, " +
                    "imageUrl = ?, " +
                    "likeCount = ? " +
                "WHERE id = ?";

        return jdbcTemplate.update(sql,
                post.getTitle(),
                post.getContent(),
                post.getImageUrl(),
                post.getLikeCount(),
                post.getId());
    }

    @Override
    public Optional<Post> findById(Long id) {
        String sql =
                        "SELECT " +
                        "p.id as p_id, " +
                        "p.title as p_title, " +
                        "p.content as p_content, " +
                        "p.imageUrl as p_imageUrl, " +
                        "p.likeCount as p_likeCount, " +
                        "c.id as c_id, " +
                        "c.content as c_content, " +
                        "t.id as t_id, " +
                        "t.title as t_title, " +
                        "(SELECT COUNT(*) FROM comment WHERE post_id = p.id) as comment_count " +
                        "FROM " + getTableName() + " as p " +
                        "LEFT JOIN comment as c ON c.post_Id = p.id " +
                        "LEFT JOIN post_tag as pt ON p.id = pt.post_id " +
                        "LEFT JOIN tag as t ON pt.tag_id = t.id " +
                        "WHERE p.id = ?";

        try {
            Post post = jdbcTemplate.query(sql, postWithCommentsRowMapper, id);
            return Optional.ofNullable(post);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Post> findAllPostsWithTags(int limit, int offset) {
        log.info("findAllPostsWithTags: Limit: {}, Offset: {}", limit, offset);

        String sql =
                        "SELECT " +
                        "p.id as p_id, " +
                        "p.title as p_title, " +
                        "p.content as p_content, " +
                        "p.imageUrl as p_imageUrl, " +
                        "p.likeCount as p_likeCount, " +
                        "t.id as t_id, " +
                        "t.title as t_title, " +
                        "(SELECT COUNT(*) FROM comment WHERE post_id = p.id) as comment_count " +
                        "FROM " + "( " +
                                "SELECT " +
                                    "id, title, content, imageUrl, likeCount " +
                                "FROM " + getTableName() + " " +
                                "ORDER BY id LIMIT ? OFFSET ?)" + " as p " +
                        "LEFT JOIN post_tag as pt ON p.id = pt.post_id " +
                        "LEFT JOIN tag as t ON pt.tag_id = t.id ";

        return jdbcTemplate.query(sql, postWithTagsRowMapper, limit, offset);
    }

    @Override
    public List<Post> findByTagIds(List<Long> tagIds, int limit, int offset) {
        String inSql = String.join(",", Collections.nCopies(tagIds.size(), "?"));

        String sql =
                        "SELECT " +
                        "p.id as p_id, " +
                        "p.title as p_title, " +
                        "p.content as p_content, " +
                        "p.imageUrl as p_imageUrl, " +
                        "p.likeCount as p_likeCount, " +
                        "t.id as t_id, " +
                        "t.title as t_title, " +
                        "(SELECT COUNT(*) FROM comment WHERE post_id = p.id) as comment_count " +
                        "FROM " + "( " +
                                "SELECT " +
                                "id, title, content, imageUrl, likeCount " +
                                "FROM " + getTableName() + " " +
                                "ORDER BY id LIMIT ? OFFSET ?)" + " as p " +
                        "LEFT JOIN post_tag as pt ON p.id = pt.post_id " +
                        "LEFT JOIN tag as t ON pt.tag_id = t.id " +
                        "WHERE t.id IN (" + inSql + ") ";

        Object[] params = new Object[tagIds.size() + 2];
        params[0] = limit;
        params[ 1] = offset;
        for (int i = 0; i < tagIds.size(); i++) {
            params[i + 2] = tagIds.get(i);
        }

        return jdbcTemplate.query(sql, postWithTagsRowMapper, params);
    }


    @Override
    public void deleteById(Long id) {
        Optional<Post> byId = findById(id);

        byId.get().getComments().forEach(comment ->
                commentService.deleteById(comment.getId()));

        byId.get().getTags().forEach(tag ->
                tagService.deleteRelationshipByTagId(tag.getId()));

        super.deleteById(id);
    }


    @Override
    public void delete() {
        findAll().forEach(post -> {
            post.getTags().forEach(tag -> tagService.deleteById(tag.getId()));

            commentService.deleteByPostId(post.getId());

            deleteById(post.getId());
        });
    }
}
