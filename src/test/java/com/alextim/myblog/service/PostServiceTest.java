package com.alextim.myblog.service;

import com.alextim.myblog.model.Post;
import com.alextim.myblog.model.Tag;
import com.alextim.myblog.repository.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class PostServiceTest {

    @Mock
    private PostRepository repository;

    @InjectMocks
    private PostServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void save_shouldCallSave() {
        Post post = new Post("title", "content");

        when(repository.save(any(Post.class))).thenReturn(post);

        Post savedPost = service.save(post);

        verify(repository).save(savedPost);
        Assertions.assertEquals(post, savedPost);
    }

    @Test
    public void findById_shouldCallFindById() {
        Post post = new Post("title", "content");

        when(repository.findById(anyLong())).thenReturn(Optional.of(post));

        Post savedPost = service.findById(1L);

        verify(repository).findById(1L);
        Assertions.assertEquals(post, savedPost);
    }

    @Test
    public void findAll_shouldCallFindAll() {
        Post post1 = new Post("title1", "content");
        Post post2 = new Post("title2", "content");

        when(repository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(post1, post2)));

        List<Post> posts = service.findAll(0, 10).getContent();

        verify(repository).findAll(PageRequest.of(0, 10));
        Assertions.assertEquals(posts, List.of(post1, post2));
    }

    @Test
    public void findByTag_shouldCallFindByTag() {
        Post post1 = new Post("title1", "content");
        Post post2 = new Post("title2", "content");

        when(repository.findByTags(anyList(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(post1, post2)));

        List<Post> posts = service.findByTag(new Tag("tag"), 0, 10).getContent();

        verify(repository).findByTags(List.of(new Tag("tag")), PageRequest.of(0, 10));
        Assertions.assertEquals(posts, List.of(post1, post2));
    }

    @Test
    public void like_shouldIncrementLikeCounter() {
        Post post = new Post("title", "content");
        post.setLikeCount(2);

        when(repository.findById(anyLong())).thenReturn(Optional.of(post));

        when(repository.save(any(Post.class))).thenReturn(post);

        Post likedPost = service.like(1L);

        verify(repository).findById(1L);
        verify(repository).save(post);
        Assertions.assertEquals(3, likedPost.getLikeCount());
    }

    @Test
    public void delete_shouldCallRepositoryDeleteComment() {
        service.delete(1L);

        verify(repository).deleteById(1L);
    }
}
