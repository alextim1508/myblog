package com.alextim.myblog.repository;


import com.alextim.myblog.model.Comment;
import com.alextim.myblog.model.Post;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;


public class CommentRepositoryTest extends RepositoryTest{

    @Test
    public void save_shouldSaveAndFindItById() {
        Post savedPost = postRepository.save(new Post("title", "content"));

        Comment comment = new Comment("comment", savedPost.getId());
        Comment savedComment = commentRepository.save(comment);

        Optional<Comment> byId = commentRepository.findById(savedComment.getId());
        Assertions.assertTrue(byId.isPresent());
        Assertions.assertEquals(comment, byId.get());
    }

    @Test
    public void update_shouldUpdateAndFindItById() {
        Post savedPost = postRepository.save(new Post("title", "content"));

        Comment savedComment = commentRepository.save(new Comment("comment", savedPost.getId()));

        savedComment.setText("NewContent");
        int count = commentRepository.update(savedComment);

        Assertions.assertEquals(1, count);

        Optional<Comment> byId = commentRepository.findById(savedComment.getId());
        Assertions.assertTrue(byId.isPresent());
        Assertions.assertEquals("NewContent", byId.get().getText());
    }

    @Test
    public void countByPostId_shouldSaveTwoCommentAndGetCountCommentsByPostId() {
        Post savedPost = postRepository.save(new Post("title", "content"));

        commentRepository.save(new Comment("comment1", savedPost.getId()));
        commentRepository.save(new Comment("comment2", savedPost.getId()));

        int count = commentRepository.countByPostId(savedPost.getId());
        Assertions.assertEquals(2, count);
    }

    @Test
    public void findAll_shouldSaveTenCommentAndFindWithLimitOffset() {
        Post savedPost = postRepository.save(new Post("title", "content"));

        for (int i = 0; i < 10; i++) {
            commentRepository.save(new Comment("comment" + i, savedPost.getId()));
        }

        int count = commentRepository.count();
        Assertions.assertEquals(10, count);

        List<Comment> comments = commentRepository.findAll(2, 2);
        Assertions.assertEquals(2, comments.size());
        Assertions.assertEquals("comment2", comments.get(0).getText());
        Assertions.assertEquals("comment3", comments.get(1).getText());
    }

    @Test
    public void findAll_shouldSaveTenCommentAndFindAll() {
        Post savedPost = postRepository.save(new Post("title", "content"));

        for (int i = 0; i < 10; i++) {
            commentRepository.save(new Comment("comment" + i, savedPost.getId()));
        }

        int count = commentRepository.count();
        Assertions.assertEquals(10, count);

        List<Comment> comments = commentRepository.findAll(Integer.MAX_VALUE, 0);
        Assertions.assertEquals(10, comments.size());

        for (int i = 0; i < 10; i++) {
            Assertions.assertEquals("comment" + i, comments.get(i).getText());
        }
    }
}
