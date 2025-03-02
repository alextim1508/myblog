package com.alextim.myblog.service;


import com.alextim.myblog.config.AppTestConfig;
import com.alextim.myblog.model.Comment;
import com.alextim.myblog.model.Post;
import com.alextim.myblog.model.Tag;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppTestConfig.class})
@Transactional
public class PostServiceIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PostService postService;

    @Autowired
    private TagService tagService;

    @Autowired
    private CommentService commentService;

    @Test
    void save_shouldSavePost() {
        Post post = new Post("title", "content");

        Post savedPost = postService.save(post);

        clearHibernateCache();

        List<Post> savedPosts = postService.findAll(0, 10).getContent();

        Assertions.assertEquals(1, savedPosts.size());
        Assertions.assertEquals(savedPost.getId(), savedPosts.get(0).getId());
        Assertions.assertEquals(savedPost, savedPosts.get(0));
    }

    @Test
    void findById_shouldFindPostById() {
        Post post = new Post("title", "content");

        Post savedPost = postService.save(post);

        clearHibernateCache();

        Post postById = postService.findById(savedPost.getId());

        Assertions.assertEquals(postById, savedPost);
    }

    @Test
    void findByPage_shouldFindPostByPage() {
        for (int i = 0; i < 10; i++) {
            postService.save(new Post("title" + i, "content"));
        }

        clearHibernateCache();

        List<Post> posts = postService.findAll(0, 2).getContent();
        Assertions.assertEquals("title0", posts.get(0).getTitle());
        Assertions.assertEquals("title1", posts.get(1).getTitle());

        posts = postService.findAll(1, 2).getContent();
        Assertions.assertEquals("title2", posts.get(0).getTitle());
        Assertions.assertEquals("title3", posts.get(1).getTitle());
    }

    @Test
    void save_shouldSavePostWithComment() {
        Post post = new Post("title", "content");

        Comment comment = new Comment("comment", post);
        post.setComments(List.of(comment));

        Post savedPost = postService.save(post);

        clearHibernateCache();

        List<Post> posts = postService.findAll(0, 10).getContent();

        Assertions.assertEquals(1, posts.size());
        Assertions.assertEquals(savedPost.getId(), posts.get(0).getId());
        Assertions.assertEquals(savedPost, posts.get(0));

        Assertions.assertEquals(1, posts.get(0).getComments().size());
        Assertions.assertEquals(comment, posts.get(0).getComments().get(0));
    }

    @Test
    void save_shouldRemoveCommentOrphan() {
        Post post = new Post("title", "content");
        Comment comment = new Comment("comment", post);
        post.setComments(List.of(comment));

        postService.save(post);

        clearHibernateCache();

        List<Post> posts = postService.findAll(0, 10).getContent();

        posts.get(0).getComments().remove(0);
        postService.save(posts.get(0));

        clearHibernateCache();

        posts = postService.findAll(0, 10).getContent();

        Assertions.assertEquals(1, posts.size());
        Assertions.assertTrue(posts.get(0).getId() != 0);
        Assertions.assertEquals(post, posts.get(0));

        Assertions.assertEquals(0, posts.get(0).getComments().size());

        List<Comment> comments = commentService.findAll(0, 10).getContent();
        Assertions.assertEquals(0, comments.size());
    }

    @Test
    void save_shouldUpdatePost() {
        Post post = new Post("title", "content");

        postService.save(post);

        clearHibernateCache();

        List<Post> posts = postService.findAll(0, 10).getContent();

        posts.get(0).setTitle("NewTitle");
        posts.get(0).setContent("NewContent");

        postService.save(posts.get(0));

        clearHibernateCache();

        posts = postService.findAll(0, 10).getContent();

        Assertions.assertEquals("NewTitle", posts.get(0).getTitle());
        Assertions.assertEquals("NewContent", posts.get(0).getContent());
    }

    @Test
    void save_shouldAddCommentToPost() {
        Post post = new Post("title", "content");

        postService.save(post);

        clearHibernateCache();

        List<Post> posts = postService.findAll(0, 10).getContent();

        Comment comment = new Comment("NewComment", posts.get(0));
        posts.get(0).getComments().add(comment);

        postService.save(posts.get(0));

        clearHibernateCache();

        posts = postService.findAll(0, 10).getContent();

        Assertions.assertEquals(1, posts.get(0).getComments().size());
        Assertions.assertEquals("NewComment", posts.get(0).getComments().get(0).getContent());
    }

    @Test
    void save_shouldUpdatePostWithCascadedComment() {
        Post post = new Post("title", "content");
        Comment comment = new Comment("comment", post);
        post.setComments(List.of(comment));

        postService.save(post);

        clearHibernateCache();

        List<Post> posts = postService.findAll(0, 10).getContent();

        posts.get(0).setContent("NewPost");
        posts.get(0).getComments().get(0).setContent("NewComment");
        postService.save(posts.get(0));

        clearHibernateCache();

        posts = postService.findAll(0, 10).getContent();

        Assertions.assertEquals(1, posts.size());
        Assertions.assertTrue(posts.get(0).getId() != 0);
        Assertions.assertEquals("NewPost", posts.get(0).getContent());

        Assertions.assertEquals(1, posts.get(0).getComments().size());
        Assertions.assertEquals("NewComment", posts.get(0).getComments().get(0).getContent());
    }

    @Test
    void save_shouldThrowExceptionWhenSavePostWithUnsavedTag() {
        Post post = new Post("title", "content");

        Tag tag = new Tag("tag");
        tag.addPost(post);

        postService.save(post);

        Assertions.assertThrowsExactly(IllegalStateException.class,
                this::clearHibernateCache);
    }

    @Test
    void findByTag_shouldFindPostByTag() {
        Post post = new Post("title", "content");
        postService.save(post);

        Tag tag1 = new Tag("tag1");
        tag1.addPost(post);
        tagService.save(tag1);

        Tag tag2 = new Tag("tag2");
        tag2.addPost(post);
        tagService.save(tag2);

        Post post2 = new Post("title", "content");
        postService.save(post2);

        Tag tag3 = new Tag("tag3");
        tag3.addPost(post2);
        tagService.save(tag3);

        Tag tag4 = new Tag("tag4");
        tag4.addPost(post2);
        tagService.save(tag4);

        clearHibernateCache();

        List<Post> posts = postService.findByTag(tag2, 0, 10).getContent();

        Assertions.assertEquals(1, posts.size());
        Assertions.assertEquals(post, posts.get(0));
    }

    @Test
    void like_shouldIncrementLikeCount() {
        Post post = new Post("title", "content");
        Post savedPost = postService.save(post);

        postService.like(savedPost.getId());

        clearHibernateCache();

        Post postById = postService.findById(savedPost.getId());
        Assertions.assertEquals(1, postById.getLikeCount());
    }

    @Test
    void delete_shouldDeletePostWithCommentButDoesNotDeleteItsTags() {
        Post post = new Post("title", "content");
        Comment comment = new Comment("comment", post);
        post.setComments(List.of(comment));

        Post savedPost = postService.save(post);

        Tag tag = new Tag("tag");
        tag.addPost(post);
        tagService.save(tag);

        clearHibernateCache();

        postService.delete(savedPost.getId());

        List<Comment> comments = commentService.findAll(0, 10).getContent();
        Assertions.assertEquals(0, comments.size());

        List<Tag> tags = tagService.findAll();
        Assertions.assertEquals(1, tags.size());
    }

    void clearHibernateCache() {
        entityManager.flush();
        entityManager.clear();
    }
}
