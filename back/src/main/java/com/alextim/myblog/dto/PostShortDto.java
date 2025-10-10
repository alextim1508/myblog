package com.alextim.myblog.dto;

import lombok.Data;

@Data
public class PostShortDto {

    public Long id;

    public String title;

    public String content;

    public String imageUrl;

    public int likeCount;

    public int commentsSize;

    public String tags;
}
