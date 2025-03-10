package com.alextim.myblog.mapper;

import com.alextim.myblog.config.AppTestConfig;
import com.alextim.myblog.dto.CommentDto;
import com.alextim.myblog.dto.NewCommentDto;
import com.alextim.myblog.model.Comment;
import com.alextim.myblog.model.Post;
import com.alextim.myblog.service.PostService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppTestConfig.class})
public class CommentMapperTest {

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    PostService postService;

    @Test
    public void toModel_shouldMapToModel() {
        Post savedPost = postService.save(new Post("title", "content"));

        Comment comment = commentMapper.toModel(new NewCommentDto("comment", savedPost.getId()));

        Assertions.assertEquals("comment", comment.getContent());
        Assertions.assertEquals(savedPost.getId(), comment.getPostId());
    }

    @Test
    public void toDto_shouldMapToDto() {
        Post savedPost = postService.save(new Post("title", "content"));
        Comment comment = new Comment("content", savedPost.getId());
        comment.setId(1L);

        CommentDto commentDto = commentMapper.toDto(comment);

        Assertions.assertEquals("content", commentDto.getContent());
        Assertions.assertEquals(comment.getId(), commentDto.getId());
    }
}
