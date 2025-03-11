package com.alextim.myblog.service;

import com.alextim.myblog.model.Post;
import com.alextim.myblog.repository.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository repository;

    @InjectMocks
    private PostServiceImpl service;

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
    public void findAll_shouldCallFindAllPostsWithTags() {
        Post post1 = new Post("title1", "content");
        Post post2 = new Post("title2", "content");

        when(repository.findAllPostsWithTags(anyInt(), anyInt())).thenReturn(List.of(post1, post2));

        List<Post> posts = service.findAll(0, 10);

        verify(repository).findAllPostsWithTags(10, 0);
        Assertions.assertEquals(posts, List.of(post1, post2));
    }

    @Test
    public void findByTag_shouldCallFindByTag() {
        Post post1 = new Post("title1", "content");
        Post post2 = new Post("title2", "content");

        when(repository.findByTagIds(anyList(), anyInt(), anyInt())).thenReturn(List.of(post1, post2));

        List<Post> posts = service.findByTag(1L, 0, 10);

        verify(repository).findByTagIds(List.of(1L), 10, 0);
        Assertions.assertEquals(posts, List.of(post1, post2));
    }

    @Test
    public void like_shouldIncrementLikeCounter() {
        Post post = new Post("title", "content");
        post.setLikeCount(2);

        when(repository.findById(anyLong())).thenReturn(Optional.of(post));

        when(repository.update(any(Post.class))).thenReturn(1);

        Post likedPost = service.like(1L);

        verify(repository).findById(1L);
        verify(repository).update(post);
        Assertions.assertEquals(3, likedPost.getLikeCount());
    }

    @Test
    public void delete_shouldCallRepositoryDeleteComment() {
        service.delete(1L);

        verify(repository).deleteById(1L);
    }
}
