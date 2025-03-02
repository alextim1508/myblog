package com.alextim.myblog.service;

import com.alextim.myblog.model.Post;
import com.alextim.myblog.model.Tag;
import com.alextim.myblog.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository repository;

    @Override
    public Post save(Post post) {
        return repository.save(post);
    }

    @Override
    public Post findById(long id) {
        return repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Post with ID " + id + " does not exist"));
    }

    @Override
    public Page<Post> findAll(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Page<Post> findByTag(Tag tag, int page, int size) {
        return repository.findByTags(List.of(tag), PageRequest.of(page, size));
    }

    @Override
    public Post like(long id) {
        Post post = repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Post with ID " + id + " does not exist"));
        post.setLikeCount(post.getLikeCount() + 1);
        return repository.save(post);
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }

}

