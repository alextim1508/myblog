package com.alextim.myblog.mapper;

import com.alextim.myblog.dto.CommentResponseDto;
import com.alextim.myblog.dto.CreateCommentRequestDto;
import com.alextim.myblog.model.Comment;
import com.alextim.myblog.model.Post;
import com.alextim.myblog.repository.CommentRepository;
import com.alextim.myblog.repository.PostRepository;
import com.alextim.myblog.repository.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class CommentMapperTest {

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    TagRepository tagRepository;


    @AfterEach
    void setUp() {
        commentRepository.delete();
        tagRepository.deleteRelationships();
        tagRepository.delete();
        postRepository.delete();
    }

    @Test
    public void toModel_shouldMapToModel() {
        Post savedPost = postRepository.save(new Post("title", "content"));

        Comment comment = commentMapper.toModel(new CreateCommentRequestDto("comment", savedPost.getId()));

        Assertions.assertEquals("comment", comment.getText());
        Assertions.assertEquals(savedPost.getId(), comment.getPostId());
    }

    @Test
    public void toDto_shouldMapToDto() {
        Post savedPost = postRepository.save(new Post("title", "content"));
        Comment comment = new Comment("content", savedPost.getId());
        comment.setId(1L);

        CommentResponseDto commentDto = commentMapper.toDto(comment);

        Assertions.assertEquals("content", commentDto.getText());
        Assertions.assertEquals(comment.getId(), commentDto.getId());
    }
}
