package com.alextim.myblog.repository.mapper;

import com.alextim.myblog.model.Post;
import com.alextim.myblog.model.Tag;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class PostWithTagsRowMapper implements ResultSetExtractor<List<Post>> {

    @AllArgsConstructor
    @EqualsAndHashCode
    private static class Pair {
        public Long postId;
        public Long anotherId;
    }

    @Override
    public List<Post> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long,Post> posts = new HashMap<>();
        Map<Pair, Tag> tags = new HashMap<>();

        while(rs.next()) {
            Long postId = rs.getLong("p_id");
            log.info("postId {}", postId);

            Post post = posts.get(postId);
            if (post == null) {
                post = new Post(
                        rs.getString("p_title"),
                        rs.getString("p_content"),
                        rs.getString("p_imageUrl")
                );
                post.setLikeCount(rs.getInt("p_likeCount"));
                post.setId(postId);
                posts.put(postId, post);
            }

            Object object = rs.getObject("t_id");
            if(object != null) {
                Long tagId = ((Long) object);
                Tag tag = tags.get(new Pair(postId, tagId));
                if (tag == null) {
                    tag = new Tag(rs.getString("t_title"));
                    tag.setId(tagId);
                    tags.put(new Pair(postId, tagId), tag);

                    post.getTags().add(tag);
                }
            }

            post.setCommentsSize(rs.getInt("comment_count"));
        }
        return new ArrayList<>(posts.values());
    }
}
