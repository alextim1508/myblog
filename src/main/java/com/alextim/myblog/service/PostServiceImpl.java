package com.alextim.myblog.service;

import com.alextim.myblog.exception.ClientException;
import com.alextim.myblog.model.Post;
import com.alextim.myblog.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository repository;

    @Override
    public Post save(Post post) {
        if(post.getId() == null) {
            return repository.save(post);
        } else {
            int updatedRowCount = repository.update(post);

            if(updatedRowCount != 1)
                throw new ClientException("Update post exception. updatedRowCount: " + updatedRowCount);

            return post;
        }
    }

    @Override
    public Post findById(long id) {
        return repository.findById(id).orElseThrow(() ->
                new ClientException("Post with ID " + id + " does not exist"));
    }

    @Override
    public List<Post> findAll(int page, int size) {
        log.info("find all post. Page: {} Size: {}", page, size);
        return repository.findAllPostsWithTags(size, page * size);
    }

    @Override
    public List<Post> findByTag(Long tagId, int page, int size) {
        log.info("find all post by tagId. TagId: {} Page: {} Size: {}", tagId, page, size);
        return repository.findByTagIds(List.of(tagId), size, page*size);
    }

    @Override
    public Post like(long id) {
        Post post = repository.findById(id).orElseThrow(() ->
                new RuntimeException("Post with ID " + id + " does not exist"));
        post.setLikeCount(post.getLikeCount() + 1);

        int updatedRowCount = repository.update(post);

        if(updatedRowCount != 1)
            throw new ClientException("Update post like exception. updatedRowCount: " + updatedRowCount);

        return post;
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }
}

