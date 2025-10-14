package com.alextim.myblog.repository.mapper;

import com.alextim.myblog.model.Image;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ImageRowMapper implements RowMapper<Image> {

    @Override
    public Image mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Image(
                rs.getLong("id"),
                rs.getString("file_name"),
                rs.getBytes("data"),
                rs.getLong("size"),
                rs.getLong("post_id")
        );
    }
}