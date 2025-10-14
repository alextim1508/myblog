package com.alextim.myblog.service;

import com.alextim.myblog.model.Post;
import com.alextim.myblog.model.Tag;
import com.alextim.myblog.repository.PostRepository;
import com.alextim.myblog.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostServiceImpl postService;

    @MockitoBean
    private PostRepository postRepository;
    @MockitoBean
    private TagRepository tagRepository;

    @Test
    public void save_shouldCallSave() {
        Post post = new Post("title", "content");

        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post savedPost = postService.save(post);

        verify(postRepository).save(savedPost);
        assertEquals(post, savedPost);
    }


    @Test
    public void findById_shouldCallFindById() {
        Post post = new Post("title", "content");

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        Post savedPost = postService.findById(1L);

        verify(postRepository).findById(1L);
        assertEquals(post, savedPost);
    }

    @Test
    public void findByTag_shouldCallFindByTag() {
        Post post1 = new Post("title1", "content");
        Post post2 = new Post("title2", "content");

        when(postRepository.findByTagIds(anyList(), anyInt(), anyInt())).thenReturn(List.of(post1, post2));

        List<Post> posts = postService.findByTag(1L, 0, 10);

        verify(postRepository).findByTagIds(List.of(1L), 10, 0);
        assertEquals(posts, List.of(post1, post2));
    }

    @Test
    public void like_shouldIncrementLikeCounter() {
        Post post = new Post("title", "content");
        post.setLikesCount(2);

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        when(postRepository.update(any(Post.class))).thenReturn(1);

        Post likedPost = postService.like(1L);

        verify(postRepository).findById(1L);
        verify(postRepository).update(post);
        assertEquals(3, likedPost.getLikesCount());
    }

    @Test
    public void delete_shouldCallRepositoryDeleteComment() {
        postService.delete(1L);

        verify(postRepository).deleteById(1L);
    }

    @Test
    void getPosts_shouldReturnPostsByTagWhenSearchStartsWithHash() {
        String search = "#java";
        int page = 1;
        int size = 10;

        Tag tag = new Tag("java");
        tag.setId(1L);

        Post post = new Post("title", "content");
        post.setId(1L);

        when(tagRepository.findTagByTitle("java")).thenReturn(Optional.of(tag));
        when(postRepository.findByTagIds(List.of(1L), size, 0)).thenReturn(List.of(post));

        List<Post> posts = postService.getPosts(search, page, size);

        assertEquals(1, posts.size());
        assertEquals(post, posts.get(0));
    }

    @Test
    void getPosts_shouldReturnEmptyListWhenTagNotFound() {
        String search = "#java";
        int page = 1;
        int size = 10;

        when(tagRepository.findTagByTitle("java")).thenReturn(Optional.empty());

        List<Post> posts = postService.getPosts(search, page, size);

        assertEquals(0, posts.size());
    }

    @Test
    void getPosts_shouldReturnPostsByTitleOrContentWhenSearchDoesNotStartWithHash() {
        String search = "java";
        int page = 1;
        int size = 10;

        Post post = new Post("title", "content");
        post.setId(1L);

        when(postRepository.findByTitleOrContent("java", size, 0)).thenReturn(List.of(post));

        List<Post> posts = postService.getPosts(search, page, size);

        assertEquals(1, posts.size());
        assertEquals(post, posts.get(0));
    }

    @Test
    void getPosts_shouldReturnAllPostsWhenSearchIsNull() {
        String search = null;
        int page = 1;
        int size = 10;

        Post post = new Post("title", "content");
        post.setId(1L);

        when(postRepository.findAll(size, 0)).thenReturn(List.of(post));

        List<Post> posts = postService.getPosts(search, page, size);

        assertEquals(1, posts.size());
        assertEquals(post, posts.get(0));
    }

    @Test
    void getPosts_shouldReturnAllPostsWhenSearchIsEmpty() {
        String search = "";
        int page = 1;
        int size = 10;

        Post post = new Post("title", "content");
        post.setId(1L);

        when(postRepository.findAll(size, 0)).thenReturn(List.of(post));

        List<Post> posts = postService.getPosts(search, page, size);

        assertEquals(1, posts.size());
        assertEquals(post, posts.get(0));
    }

    @Test
    void getPosts_shouldReturnEmptyListWhenNoPostsFound() {
        String search = "java";
        int page = 1;
        int size = 10;

        when(postRepository.findByTitleOrContent("java", size, 0)).thenReturn(Collections.emptyList());

        List<Post> posts = postService.getPosts(search, page, size);

        assertEquals(0, posts.size());
    }
}

