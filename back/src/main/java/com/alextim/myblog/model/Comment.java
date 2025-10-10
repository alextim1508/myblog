package com.alextim.myblog.model;

import lombok.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"content"})
public class Comment {

    private Long id;

    @NonNull
    private String content;

    @NonNull
    private Long postId;
}
