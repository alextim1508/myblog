package com.alextim.myblog.model;

import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "comments")
@EqualsAndHashCode(of = {"title", "content"})
public class Post {

    private Long id;

    @NonNull
    private String title;

    @NonNull
    private String content;

    private String imageUrl;

    @Singular
    private List<Comment> comments = new ArrayList<>();

    @Singular
    private Set<Tag> tags = new HashSet<>();

    private int likeCount;

    private int commentsSize;

    public Post(String title, String content, String imageUrl) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }
}
