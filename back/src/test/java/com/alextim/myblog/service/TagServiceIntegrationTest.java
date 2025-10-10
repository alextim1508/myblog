package com.alextim.myblog.service;

import com.alextim.myblog.model.Post;
import com.alextim.myblog.model.Tag;
import com.alextim.myblog.repository.CommentRepository;
import com.alextim.myblog.repository.PostRepository;
import com.alextim.myblog.repository.TagRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

@SpringBootTest
public class TagServiceIntegrationTest {

    @Autowired
    TagService tagService;
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
    public void save_shouldSaveTag() {
        Post savedPost = postService.save(new Post("title", "content"));
        Tag tag = new Tag("tag");

        Tag savedTag = tagService.save(tag, savedPost.getId());

        List<Tag> tags = tagService.findAll();

        Assertions.assertEquals(1, tags.size());
        Assertions.assertEquals(tag, tags.get(0));
        Assertions.assertEquals(savedTag.getId(), tags.get(0).getId());
    }

    @Test
    public void findTagByTitle_shouldFindByTitleTag() {
        Post savedPost = postService.save(new Post("title", "content"));

        Tag tag1 = new Tag("tag1");
        tagService.save(tag1, savedPost.getId());

        Tag tag2 = new Tag("tag2");
        tagService.save(tag2, savedPost.getId());

        Tag tag3 = new Tag("tag3");
        tagService.save(tag3, savedPost.getId());

        Tag tagById = tagService.findTagByTitle("tag1").get();

        Assertions.assertEquals(tagById, tag1);
    }

    @Test
    public void findTagsByTitles_shouldFindByTitlesTag() {
        Post savedPost = postService.save(new Post("title", "content"));

        Tag tag1 = new Tag("tag1");
        tagService.save(tag1, savedPost.getId());

        Tag tag2 = new Tag("tag2");
        tagService.save(tag2, savedPost.getId());

        Tag tag3 = new Tag("tag3");
        tagService.save(tag3, savedPost.getId());

        Set<Tag> tagsByTitles = tagService.findTagsByTitles(Set.of("tag1", "tag2", "tag100"));

        Assertions.assertEquals(2, tagsByTitles.size());
        Assertions.assertTrue(tagsByTitles.contains(tag1));
        Assertions.assertTrue(tagsByTitles.contains(tag2));
    }


    @Test
    public void save_shouldSaveByStringContent() {
        Post savedPost = postService.save(new Post("title", "content"));

        Tag tag1 = new Tag("tag1");
        tagService.save(tag1, savedPost.getId());

        Set<Tag> savedTags = tagService.save("tag1,tag2, tag3, ");

        Assertions.assertEquals(3, savedTags.size());
        Assertions.assertTrue(savedTags.contains(new Tag("tag1")));
        Assertions.assertTrue(savedTags.contains(new Tag("tag2")));
        Assertions.assertTrue(savedTags.contains(new Tag("tag3")));
    }

    @Test
    public void findAll_shouldSaveTagAndFindAll() {
        Post savedPost = postService.save(new Post("title", "content"));

        Tag tag1 = new Tag("tag1");

        tagService.save(tag1, savedPost.getId());

        List<Tag> tags = tagService.findAll();

        Assertions.assertEquals(1, tags.size());
    }
}
