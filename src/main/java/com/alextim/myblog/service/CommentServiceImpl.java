package com.alextim.myblog.service;

import com.alextim.myblog.model.Comment;
import com.alextim.myblog.model.Post;
import com.alextim.myblog.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;

    @Override
    public Comment save(Comment comment) {
        return repository.save(comment);
    }

    @Override
    public Comment findById(long id) {
        return repository.findById(id).orElseThrow(() ->
                new RuntimeException("Comment with ID " + id + " does not exist"));
    }

    @Override
    public int countByPost(Post post) {
        return repository.countByPost(post);
    }

    @Override
    public Page<Comment> findAll(int page, int size) {
        return repository.findAll(page, size);
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }
}
