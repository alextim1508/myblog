package com.alextim.myblog.mapper;

import com.alextim.myblog.dto.CreatePostRequestDto;
import com.alextim.myblog.dto.PostResponseDto;
import com.alextim.myblog.model.Comment;
import com.alextim.myblog.model.Post;
import com.alextim.myblog.model.Tag;
import com.alextim.myblog.repository.CommentRepository;
import com.alextim.myblog.repository.PostRepository;
import com.alextim.myblog.repository.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class PostMapperTest {

    @Autowired
    PostMapper postMapper;

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
    void toModelTest() {
        Post post = postMapper.toModel(new CreatePostRequestDto("title", "content", List.of("tag1", "tag2")));

        Assertions.assertEquals("title", post.getTitle());
        Assertions.assertEquals("content", post.getText());

        List<Tag> tagsByTitleIn = tagRepository.findAll();
        Assertions.assertEquals(2, tagsByTitleIn.size());
    }

    @Test
    void toShortDto_shouldMapToShortDto() {
        Post post = new Post("title", "content");
        post.setLikesCount(10);
        Post savedPost = postRepository.save(post);

        commentRepository.save(new Comment("comment1", savedPost.getId()));
        commentRepository.save(new Comment("comment2", savedPost.getId()));

        Tag tag1 = new Tag("tag1");
        Tag savedTag = tagRepository.save(tag1);
        tagRepository.saveRelationship(savedTag.getId(), savedPost.getId());

        Tag tag2 = new Tag("tag2");
        savedTag = tagRepository.save(tag2);
        tagRepository.saveRelationship(savedTag.getId(), savedPost.getId());

        Tag tag3 = new Tag("tag3");
        savedTag = tagRepository.save(tag3);
        tagRepository.saveRelationship(savedTag.getId(), savedPost.getId());

        PostResponseDto postResponseDto = postMapper.toDto(postRepository.findById(savedPost.getId()).get());

        Assertions.assertEquals("title", postResponseDto.getTitle());
        Assertions.assertEquals("content", postResponseDto.getText());
        Assertions.assertEquals(10, postResponseDto.getLikesCount());
        Assertions.assertEquals(2, postResponseDto.getCommentsCount());
        Assertions.assertEquals(List.of("tag1", "tag2", "tag3"), postResponseDto.getTags());
    }
}
