package com.alextim.myblog.repository;

import com.alextim.myblog.model.Comment;

public interface CommentRepository extends AbstractRepository<Comment> {

    Comment save(Comment comment);

    int update(Comment comment);

    int countByPostId(Long postId);

    void deleteByPostId(long id);
}
