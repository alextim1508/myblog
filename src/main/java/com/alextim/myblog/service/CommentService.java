package com.alextim.myblog.service;

import com.alextim.myblog.model.Comment;
import org.springframework.data.domain.Page;


public interface CommentService {

    Comment save(Comment comment);

    Page<Comment> findAll(int page, int size);

    void delete(long id);
}
