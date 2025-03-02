package com.alextim.myblog.service;

import com.alextim.myblog.model.Comment;
import com.alextim.myblog.model.Post;
import org.springframework.data.domain.Page;


public interface CommentService {

    Comment save(Comment comment);

    int countByPost(Post post);

    Page<Comment> findAll(int page, int size);

    void delete(long id);
}
