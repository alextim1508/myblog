package com.alextim.myblog.service;

import com.alextim.myblog.model.Post;

import java.util.List;

public interface PostService {

    Post save(Post post);

    Post findById(long id);

    List<Post> findAll(int page, int size);

    List<Post> findByTag(Long tagId, int page, int size);

    Post like(long id);

    void delete(long id);
}
