package com.alextim.myblog.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@Getter
public class PostListResponseDto {
    private List<PostResponseDto> posts;
    private boolean hasPrev;
    private boolean hasNext;
    private int lastPage;
}
