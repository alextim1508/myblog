package com.alextim.myblog.service;

import com.alextim.myblog.exception.ClientException;
import com.alextim.myblog.model.Post;
import com.alextim.myblog.repository.PostRepository;
import com.alextim.myblog.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository repository;
    private final TagRepository tagRepository;

    @Override
    public Post save(Post post) {
        log.debug("Saving post with ID: {}", post.getId());

        if (post.getId() == null) {
            log.info("Creating new post with title: {}", post.getTitle());
            Post savedPost = repository.save(post);
            log.info("Successfully created post with ID: {}", savedPost.getId());
            return savedPost;
        } else {
            log.info("Updating existing post with ID: {}", post.getId());
            int updatedRowCount = repository.update(post);

            if (updatedRowCount != 1) {
                log.error("Failed to update post with ID: {}. Updated row count: {}", post.getId(), updatedRowCount);
                throw new ClientException("Update post exception. updatedRowCount: " + updatedRowCount);
            }

            log.info("Successfully updated post with ID: {}", post.getId());
            return post;
        }
    }

    @Override
    public List<Post> getPosts(String search, int page, int size) {
        log.info("Getting posts with page {} size {} by search query '{}'", page, size, search);

        List<Post> posts;

        if (search != null && !search.trim().isEmpty()) {
            search = search.trim();
            if (search.startsWith("#")) {
                String tagTitle = search.substring(1);
                log.debug("Searching by tag: '{}'", tagTitle);
                posts = tagRepository.findTagByTitle(tagTitle)
                        .map(tag -> findByTag(tag.getId(), page - 1, size))
                        .orElse(Collections.emptyList());
            } else {
                log.debug("Searching by title or content: '{}'", search);
                posts = findByTitleOrContent(search, page - 1, size);
            }
        } else {
            log.debug("No search query, fetching all posts");
            posts = findAll(page - 1, size);
        }

        log.debug("Get {} posts", posts.size());
        return posts;
    }

    @Override
    public Post findById(long id) {
        log.info("Finding post by ID: {}", id);
        Post post = repository.findById(id).orElseThrow(() ->
                new ClientException("Post with ID " + id + " does not exist"));
        log.debug("Found post: {}", post.getTitle());
        return post;
    }

    @Override
    public List<Post> findAll(int page, int size) {
        log.info("Finding all posts. Page: {}, Size: {}", page, size);
        List<Post> posts = repository.findAll(size, page * size);
        log.debug("Found {} posts", posts.size());
        return posts;
    }

    @Override
    public List<Post> findByTag(long tagId, int page, int size) {
        log.info("Finding posts by tag ID: {}, Page: {}, Size: {}", tagId, page, size);
        List<Post> posts = repository.findByTagIds(List.of(tagId), size, page * size);
        log.debug("Found {} posts for tag ID: {}", posts.size(), tagId);
        return posts;
    }

    @Override
    public List<Post> findByTitleOrContent(String search, int page, int size) {
        log.info("Finding posts by title or content : {}, Page: {}, Size: {}", search, page, size);
        List<Post> posts = repository.findByTitleOrContent(search, size, page * size);
        log.debug("Found {} posts for title or content: {}", posts.size(), search);
        return posts;
    }

    @Override
    public long count() {
        log.info("Counting all posts");
        long count = repository.count();
        log.info("Total posts count: {}", count);
        return count;
    }

    @Override
    public Post like(long id) {
        log.info("Incrementing like for post with ID: {}", id);
        Post post = repository.findById(id).orElseThrow(() ->
                new RuntimeException("Post with ID " + id + " does not exist"));

        post.setLikesCount(post.getLikesCount() + 1);
        log.debug("New like count for post ID {}: {}", post.getId(), post.getLikesCount());

        int updatedRowCount = repository.update(post);

        if (updatedRowCount != 1) {
            log.error("Failed to update like count for post with ID: {}. Updated row count: {}", id, updatedRowCount);
            throw new ClientException("Update post like exception. updatedRowCount: " + updatedRowCount);
        }

        log.info("Successfully incremented like for post with ID: {}", id);
        return post;
    }

    @Override
    public void delete(long id) {
        log.info("Deleting post with ID: {}", id);
        repository.deleteById(id);
        log.info("Successfully deleted post with ID: {}", id);
    }
}

