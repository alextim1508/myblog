package com.alextim.myblog.repository;

import com.alextim.myblog.config.AppTestConfig;
import com.alextim.myblog.model.Post;
import com.alextim.myblog.model.Tag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Optional;
import java.util.Set;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppTestConfig.class})
public class TagRepositoryTest {

    @Autowired
    TagRepository tagRepository;

    @Autowired
    PostRepository postRepository;

    @BeforeEach
    void setUp() {
        tagRepository.deleteRelationships();
        tagRepository.delete();
        postRepository.delete();
    }

    @Test
    public void save_shouldSaveTagWithPostAndFindByPostId() {
        Post savedPost = postRepository.save(new Post("title", "content", "url"));

        Tag tag = tagRepository.save(new Tag("tag"));
        tagRepository.saveRelationship(tag.getId(), savedPost.getId());

        Set<Tag> tagsByPostId = tagRepository.findTagsByPostId(savedPost.getId());
        Assertions.assertEquals(1, tagsByPostId.size());
        Assertions.assertTrue(tagsByPostId.contains(tag));
    }

    @Test
    public void save_shouldSaveTagWithPostAndFindById() {
        Tag tag = new Tag("tag1");
        Tag savedTag = tagRepository.save(tag);
        Optional<Tag> byId = tagRepository.findById(savedTag.getId());

        Assertions.assertTrue(byId.isPresent());
        Assertions.assertEquals(tag, byId.get());
    }

    @Test
    public void findTagByTitle_shouldSaveTagAndFindItByTitle() {
        Tag tag = new Tag("tag1");
        tagRepository.save(tag);

        Optional<Tag> byTitle = tagRepository.findTagByTitle("tag1");

        Assertions.assertTrue(byTitle.isPresent());
        Assertions.assertEquals(tag, byTitle.get());
    }

    @Test
    public void findTagByTitleIn_shouldSaveTagAndFindItByTitleIn() {
        Tag tag1 = new Tag("tag1");
        tagRepository.save(tag1);

        Tag tag2 = new Tag("tag2");
        tagRepository.save(tag2);

        Set<Tag> tags = tagRepository.findTagsByTitleIn(Set.of("tag1", "tag2"));

        Assertions.assertEquals(2, tags.size());
        Assertions.assertTrue(tags.contains(tag1));
        Assertions.assertTrue(tags.contains(tag2));
    }
}
