package com.alextim.myblog.mapper;

import com.alextim.myblog.config.AppTestConfig;
import com.alextim.myblog.dto.NewPostDto;
import com.alextim.myblog.dto.PostDto;
import com.alextim.myblog.dto.PostShortDto;
import com.alextim.myblog.model.Comment;
import com.alextim.myblog.model.Post;
import com.alextim.myblog.model.Tag;
import com.alextim.myblog.service.CommentService;
import com.alextim.myblog.service.PostService;
import com.alextim.myblog.service.TagService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppTestConfig.class})
public class PostMapperTest {

    @Autowired
    PostMapper postMapper;

    @Autowired
    PostService postService;

    @Autowired
    TagService tagService;

    @Autowired
    CommentService commentService;

    @Test
    void toModelTest() {
        Post post = postMapper.toModel(new NewPostDto("title", "content", "tag1, tag2"));
        Assertions.assertEquals(2, post.getTags().size());
        Assertions.assertTrue(post.getTags().contains(new Tag("tag1")));
        Assertions.assertTrue(post.getTags().contains(new Tag("tag2")));

    }

    @Test
    void toShortDto_shouldMapToShortDto() {
        Post post = new Post("title", "content");
        post.setComments(List.of(new Comment("comment1", post), new Comment("comment2", post)));
        postService.save(post);

        Tag tag1 = new Tag("tag1");
        tag1.addPost(post);
        tagService.save(tag1);

        Tag tag2 = new Tag("tag2");
        tag2.addPost(post);
        tagService.save(tag2);

        Tag tag3 = new Tag("tag3");
        tag3.addPost(post);
        tagService.save(tag3);

        PostShortDto postShortDto = postMapper.toShortDto(post);
        Assertions.assertEquals(2, postShortDto.commentsSize);
        Assertions.assertTrue(postShortDto.tags.contains("tag1"));
        Assertions.assertTrue(postShortDto.tags.contains("tag2"));
        Assertions.assertTrue(postShortDto.tags.contains("tag3"));

    }

    @Test
    void toDto_shouldMapToDto() {
        Post post = new Post("title", "content");
        post.setComments(List.of(new Comment("comment", post)));
        postService.save(post);

        Tag tag1 = new Tag("tag1");
        tag1.addPost(post);
        tagService.save(tag1);

        Tag tag2 = new Tag("tag2");
        tag2.addPost(post);
        tagService.save(tag2);


        PostDto postDto = postMapper.toDto(post);

        Assertions.assertEquals(1, postDto.getComments().size());
        Assertions.assertTrue(postDto.tags.contains("tag1"));
        Assertions.assertTrue(postDto.tags.contains("tag2"));
    }
}
