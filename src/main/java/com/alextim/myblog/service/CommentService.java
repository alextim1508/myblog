package com.alextim.myblog.service;

import com.alextim.myblog.model.Comment;

import java.util.List;


public interface CommentService {

    Comment save(Comment comment);

    Comment findById(long id);

    int countByPostId(long postId);

    List<Comment> findAll(int page, int size);

    void deleteByPostId(long id);

    void deleteById(long id);
}
