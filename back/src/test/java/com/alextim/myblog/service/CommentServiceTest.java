package com.alextim.myblog.service;

import com.alextim.myblog.model.Comment;
import com.alextim.myblog.model.Post;
import com.alextim.myblog.repository.CommentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CommentServiceTest {

    @MockitoBean
    private CommentRepository repository;

    @Autowired
    private CommentServiceImpl service;

    @Test
    public void save_shouldCallSave() {
        Post post = new Post("title", "content");
        post.setId(1L);
        Comment comment = new Comment("comment", post.getId());

        when(repository.save(any(Comment.class))).thenReturn(comment);

        Comment savedComment = service.save(comment);

        verify(repository).save(comment);
        Assertions.assertEquals(comment, savedComment);
    }

    @Test
    public void findAll_shouldFindAllComment() {
        Post post = new Post("title", "content");
        post.setId(1L);
        Comment comment1 = new Comment("comment1", post.getId());
        Comment comment2 = new Comment("comment1", post.getId());

        when(repository.findAll(anyInt(), anyInt())).thenReturn(List.of(comment1, comment2));

        List<Comment> comments = service.findAll(0, 10);

        verify(repository).findAll(10, 0);
        Assertions.assertEquals(comments, List.of(comment1, comment2));
    }

    @Test
    public void delete_shouldCallRepositoryDeleteComment() {
        service.deleteById(1L);

        verify(repository).deleteById(1L);
    }
}
