package com.alextim.myblog.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@Getter
public class PostResponseDto {
    private Long id;
    private String title;
    private String text;
    private List<String> tags;
    private int likesCount;
    private int commentsCount;
}
