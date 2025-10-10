package com.alextim.myblog.service;

import com.alextim.myblog.model.Comment;
import com.alextim.myblog.model.Post;
import com.alextim.myblog.repository.CommentRepository;
import com.alextim.myblog.repository.PostRepository;
import com.alextim.myblog.repository.TagRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CommentServiceIntegrationTest {

    @Autowired
    CommentService commentService;
    @Autowired
    PostService postService;

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
    void save_shouldSaveComment() {
        Post post = new Post("title", "content");
        Post savedPost = postService.save(post);

        Comment comment = new Comment("Comment", savedPost.getId());
        commentService.save(comment);

        List<Comment> comments = commentService.findAll(0, 10);
        Assertions.assertEquals(1, comments.size());
        Assertions.assertEquals(comment, comments.get(0));
        Assertions.assertEquals(comment.getId(), comments.get(0).getId());
    }

    @Test
    void delete_shouldDeleteCommentButDoesNotDeletePost() {
        Post post = new Post("title", "content");
        Post savedPost = postService.save(post);

        Comment comment = new Comment("Comment", savedPost.getId());
        Comment savedComment = commentService.save(comment);

        commentService.deleteById(savedComment.getId());


        List<Comment> comments = commentService.findAll(0, 10);
        Assertions.assertEquals(0, comments.size());

        List<Post> posts = postService.findAll(0, 10);
        Assertions.assertEquals(1, posts.size());
        Assertions.assertEquals(post, posts.get(0));
        Assertions.assertEquals(0, posts.get(0).getComments().size());
    }
}
