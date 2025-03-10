package com.alextim.myblog.service;

import com.alextim.myblog.exception.ClientException;
import com.alextim.myblog.model.Comment;
import com.alextim.myblog.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;

    @Override
    public Comment save(Comment comment) {
        if(comment.getId() == null) {
            return repository.save(comment);
        } else {
            int updatedRowCount = repository.update(comment);

            if(updatedRowCount != 1)
                throw new ClientException("Update comment exception. updatedRowCount: " + updatedRowCount);

            return comment;
        }
    }

    @Override
    public Comment findById(long id) {
        return repository.findById(id).orElseThrow(() ->
                new ClientException("Comment with ID " + id + " does not exist"));
    }

    @Override
    public int countByPostId(long postId) {
        return repository.countByPostId(postId);
    }

    @Override
    public List<Comment> findAll(int page, int size) {
        return repository.findAll(size, page*size);
    }

    @Override
    public void deleteByPostId(long id) {
        repository.deleteByPostId(id);
    }

    @Override
    public void deleteById(long id) {
        repository.deleteById(id);
    }
}
