package com.alextim.myblog.service;

import com.alextim.myblog.model.Post;
import com.alextim.myblog.model.Tag;
import org.springframework.data.domain.Page;

public interface PostService {

    Post save(Post post);

    Post findById(long id);

    Page<Post> findAll(int page, int size);

    Page<Post> findByTag(Tag tag, int page, int size);

    Post like(long id);

    void delete(long id);
}
