package com.alextim.myblog.mapper;

import com.alextim.myblog.config.AppTestConfig;
import com.alextim.myblog.dto.NewPostDto;
import com.alextim.myblog.dto.PostDto;
import com.alextim.myblog.dto.PostShortDto;
import com.alextim.myblog.model.Comment;
import com.alextim.myblog.model.Post;
import com.alextim.myblog.model.Tag;
import com.alextim.myblog.repository.CommentRepository;
import com.alextim.myblog.repository.PostRepository;
import com.alextim.myblog.repository.TagRepository;
import com.alextim.myblog.service.CommentService;
import com.alextim.myblog.service.PostService;
import com.alextim.myblog.service.TagService;
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
public class PostMapperTest {

    @Autowired
    PostMapper postMapper;

    @Autowired
    PostService postService;

    @Autowired
    TagService tagService;

    @Autowired
    CommentService commentService;

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
    void toModelTest() {
        Post post = postMapper.toModel(new NewPostDto("title", "content", "url", "tag1, tag2"));
        Assertions.assertEquals(2, post.getTags().size());
        Assertions.assertTrue(post.getTags().contains(new Tag("tag1")));
        Assertions.assertTrue(post.getTags().contains(new Tag("tag2")));
    }

    @Test
    void toShortDto_shouldMapToShortDto() {
        Post savedPost = postService.save(new Post("title", "content"));

        commentService.save(new Comment("comment1", savedPost.getId()));
        commentService.save(new Comment("comment2", savedPost.getId()));

        Tag tag1 = new Tag("tag1");
        tagService.save(tag1, savedPost.getId());

        Tag tag2 = new Tag("tag2");
        tagService.save(tag2, savedPost.getId());

        Tag tag3 = new Tag("tag3");
        tagService.save(tag3, savedPost.getId());

        PostShortDto postShortDto = postMapper.toShortDto(postService.findById(savedPost.getId()));

        Assertions.assertEquals(2, postShortDto.commentsSize);
        Assertions.assertTrue(postShortDto.tags.contains("tag1"));
        Assertions.assertTrue(postShortDto.tags.contains("tag2"));
        Assertions.assertTrue(postShortDto.tags.contains("tag3"));

    }

    @Test
    void toDto_shouldMapToDto() {
        Post savedPost = postService.save(new Post("title", "content"));
        commentService.save(new Comment("comment1", savedPost.getId()));

        Tag tag1 = new Tag("tag1");
        tagService.save(tag1, savedPost.getId());

        Tag tag2 = new Tag("tag2");
        tagService.save(tag2, savedPost.getId());

        Post byId = postService.findById(savedPost.getId());

        PostDto postDto = postMapper.toDto(byId);

        Assertions.assertEquals(1, postDto.getComments().size());
        Assertions.assertTrue(postDto.tags.contains("tag1"));
        Assertions.assertTrue(postDto.tags.contains("tag2"));
    }
}
