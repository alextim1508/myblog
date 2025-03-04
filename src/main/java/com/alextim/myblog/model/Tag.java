package com.alextim.myblog.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@ToString(exclude = {"posts"})
@EqualsAndHashCode(of = {"title"})
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String title;

    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts = new HashSet<>();

    public Tag(String title) {
        this.title = title;
    }

    public void addPost(Post post) {
        posts.add(post);
        post.getTags().add(this);
    }
}
