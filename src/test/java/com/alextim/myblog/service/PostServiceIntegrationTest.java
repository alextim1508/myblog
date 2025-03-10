package com.alextim.myblog.service;


import com.alextim.myblog.config.AppTestConfig;
import com.alextim.myblog.model.Comment;
import com.alextim.myblog.model.Post;
import com.alextim.myblog.model.Tag;
import com.alextim.myblog.repository.CommentRepository;
import com.alextim.myblog.repository.PostRepository;
import com.alextim.myblog.repository.TagRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppTestConfig.class})
public class PostServiceIntegrationTest {

    @Autowired
    private PostService postService;
    @Autowired
    private TagService tagService;

    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        commentRepository.delete();
        postRepository.delete();
        tagRepository.deleteRelationships();
        tagRepository.delete();
    }

    @Test
    void save_shouldSavePost() {
        Post post = new Post("title", "content");
        Post savedPost = postService.save(post);

        List<Post> savedPosts = postService.findAll(0, 10);

        Assertions.assertEquals(1, savedPosts.size());
        Assertions.assertEquals(savedPost.getId(), savedPosts.get(0).getId());
        Assertions.assertEquals(savedPost, savedPosts.get(0));
    }

    @Test
    void findById_shouldFindPostById() {
        Post post = new Post("title", "content");
        Post savedPost = postService.save(post);

        Post postById = postService.findById(savedPost.getId());

        Assertions.assertEquals(postById, savedPost);
    }

    @Test
    void findByPage_shouldFindPostByPage() {
        for (int i = 0; i < 10; i++) {
            postService.save(new Post("title" + i, "content"));
        }

        List<Post> posts = postService.findAll(0, 2);
        Assertions.assertEquals("title0", posts.get(0).getTitle());
        Assertions.assertEquals("title1", posts.get(1).getTitle());

        posts = postService.findAll(1, 2);
        Assertions.assertEquals("title2", posts.get(0).getTitle());
        Assertions.assertEquals("title3", posts.get(1).getTitle());
    }


    @Test
    void save_shouldUpdatePost() {
        Post post = new Post("title", "content");
        postService.save(post);

        List<Post> posts = postService.findAll(0, 10);

        posts.get(0).setTitle("NewTitle");
        posts.get(0).setContent("NewContent");

        postService.save(posts.get(0));

        posts = postService.findAll(0, 10);

        Assertions.assertEquals("NewTitle", posts.get(0).getTitle());
        Assertions.assertEquals("NewContent", posts.get(0).getContent());
    }


    @Test
    void findByTag_shouldFindPostByTag() {
        Post post = new Post("title", "content");
        Post savedPost1 = postService.save(post);

        Tag tag1 = new Tag("tag1");
        tagService.save(tag1, savedPost1.getId());

        Tag tag2 = new Tag("tag2");
        tagService.save(tag2, savedPost1.getId());

        Post post2 = new Post("title", "content");
        Post savedPost2 = postService.save(post2);

        Tag tag3 = new Tag("tag3");
        tagService.save(tag3, savedPost2.getId());

        Tag tag4 = new Tag("tag4");
        tagService.save(tag4, savedPost2.getId());

        List<Post> posts = postService.findByTag(tag2.getId(), 0, 10);

        Assertions.assertEquals(1, posts.size());
        Assertions.assertEquals(post, posts.get(0));
    }

    @Test
    void like_shouldIncrementLikeCount() {
        Post post = new Post("title", "content");
        Post savedPost = postService.save(post);

        postService.like(savedPost.getId());

        Post postById = postService.findById(savedPost.getId());
        System.out.println("postById = " + postById);

        Assertions.assertEquals(1, postById.getLikeCount());
    }
}
