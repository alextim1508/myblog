package com.alextim.myblog.model;

import lombok.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"title", "content"})
public class Post {

    private Long id;

    @NonNull
    private String title;

    @NonNull
    private String text;

    private int likesCount;
}
