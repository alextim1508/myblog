package com.alextim.myblog.model;

import lombok.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"text", "postId"})
public class Comment {

    private Long id;

    @NonNull
    private String text;

    @NonNull
    private Long postId;
}
