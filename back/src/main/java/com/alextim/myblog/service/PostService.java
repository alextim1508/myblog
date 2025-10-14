package com.alextim.myblog.service;

import com.alextim.myblog.model.Post;

import java.util.List;

public interface PostService {

    Post save(Post post);

    List<Post>  getPosts(String search, int page, int size);

    Post findById(long id);

    List<Post> findAll(int page, int size);

    List<Post> findByTag(long tagId, int page, int size);

    List<Post> findByTitleOrContent(String search, int i, int size);

    long count();

    Post like(long id);

    void delete(long id);
}
