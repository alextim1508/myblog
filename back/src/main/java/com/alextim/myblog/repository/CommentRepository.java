package com.alextim.myblog.repository;

import com.alextim.myblog.model.Comment;

import java.util.List;

public interface CommentRepository extends AbstractRepository<Comment> {

    Comment save(Comment comment);

    int update(Comment comment);

    int countByPostId(long postId);

    void deleteByPostId(long id);

    List<Comment> findByPostId(long postId);

}
