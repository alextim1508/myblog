package com.alextim.myblog.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"content"})
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String content;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Post post;

    public Comment(String content, Post post) {
        this.content = content;
        this.post = post;
    }
}
