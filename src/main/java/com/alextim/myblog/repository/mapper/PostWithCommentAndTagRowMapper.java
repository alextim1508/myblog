package com.alextim.myblog.repository.mapper;

import com.alextim.myblog.model.Comment;
import com.alextim.myblog.model.Post;
import com.alextim.myblog.model.Tag;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class PostWithCommentAndTagRowMapper implements ResultSetExtractor<Post> {

    @Override
    public Post extractData(ResultSet rs) throws SQLException, DataAccessException {
        Post post = null;
        Map<Long, Tag> tags = new HashMap<>();
        Map<Long, Comment> comments = new HashMap<>();

        while(rs.next()) {
            if (post == null) {
                post = new Post(
                        rs.getString("p_title"),
                        rs.getString("p_content"),
                        rs.getString("p_imageUrl")
                );
                post.setLikeCount(rs.getInt("p_likeCount"));
                post.setId(rs.getLong("p_id"));
            }

            Object object = rs.getObject("t_id");
            if(object != null) {
                Long tagId = (Long) object;
                Tag tag = tags.get(object);
                if (tag == null) {
                    tag = new Tag(rs.getString("t_title"));
                    tag.setId(tagId);
                    tags.put(tagId, tag);

                    post.getTags().add(tag);
                }
            }

            object = rs.getObject("c_id");
            if(object != null) {
                Long commentId = ((Long) object);
                Comment comment = comments.get(commentId);
                if (comment == null) {
                    comment = new Comment(rs.getString("c_content"), rs.getLong("p_id"));
                    comment.setId(commentId);
                    comments.put(commentId, comment);

                    post.getComments().add(comment);
                }
            }

            post.setCommentsSize(rs.getInt("comment_count"));
        }

        return post;
    }
}