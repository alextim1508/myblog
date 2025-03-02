package com.alextim.myblog.service;

import com.alextim.myblog.config.AppTestConfig;
import com.alextim.myblog.model.Comment;
import com.alextim.myblog.model.Post;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppTestConfig.class})
@Transactional
public class CommentServiceIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Test
    void save_shouldSaveComment() {
        Post post = new Post("title", "content");
        Post savedPost = postService.save(post);

        Comment comment = new Comment("Comment", savedPost);
        commentService.save(comment);

        List<Comment> comments = commentService.findAll(0, 10).getContent();
        Assertions.assertEquals(1, comments.size());
        Assertions.assertEquals(comment, comments.get(0));
        Assertions.assertEquals(comment.getId(), comments.get(0).getId());
    }

    @Test
    void delete_shouldDeleteCommentButDoesNotDeletePost() {
        Post post = new Post("title", "content");
        Post savedPost = postService.save(post);

        Comment comment = new Comment("Comment", savedPost);
        Comment savedComment = commentService.save(comment);

        commentService.delete(savedComment.getId());

        clearHibernateCache();

        List<Comment> comments = commentService.findAll(0, 10).getContent();
        Assertions.assertEquals(0, comments.size());

        List<Post> posts = postService.findAll(0, 10).getContent();
        Assertions.assertEquals(1, posts.size());
        Assertions.assertEquals(post, posts.get(0));
        Assertions.assertEquals(0, posts.get(0).getComments().size());
    }

    void clearHibernateCache() {
        entityManager.flush();
        entityManager.clear();
    }
}
