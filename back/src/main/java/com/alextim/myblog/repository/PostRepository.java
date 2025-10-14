package com.alextim.myblog.repository;

import com.alextim.myblog.model.Post;

import java.util.List;

public interface PostRepository extends AbstractRepository<Post> {

    Post save(Post post);

    int update(Post post);

    List<Post> findByTagIds(List<Long> tagIds, int limit, int offset);

    List<Post> findByTitleOrContent(String query, int limit, int offset);

    void deleteById(long id);
}

