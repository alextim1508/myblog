package com.alextim.myblog.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@Getter
public class CommentResponseDto {
    private Long id;
    private String text;
    private Long postId;
}
