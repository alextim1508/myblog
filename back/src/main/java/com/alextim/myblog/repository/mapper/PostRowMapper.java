package com.alextim.myblog.repository.mapper;

import com.alextim.myblog.model.Post;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

@Component
public class PostRowMapper implements RowMapper<Post> {

    @Override
    public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Post.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .content(rs.getString("content"))
                .imageUrl(rs.getString("imageUrl"))
                .likeCount(rs.getInt("likeCount"))
                .tags(new HashSet<>())
                .comments(new ArrayList<>())
                .build();
    }
}
