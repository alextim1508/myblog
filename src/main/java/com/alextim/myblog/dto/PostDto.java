package com.alextim.myblog.dto;

import lombok.Data;

import java.util.List;

@Data
public class PostDto {

    public Long id;

    public String title;

    public String content;

    public int likeCount;

    public List<CommentDto> comments;

    public String tags;
}

