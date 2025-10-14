package com.alextim.myblog.repository;

import com.alextim.myblog.model.Post;
import com.alextim.myblog.model.Tag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public class TagRepositoryTest extends RepositoryTest {

    @Test
    public void save_shouldSaveTagWithPostAndFindByPostId() {
        Post savedPost = postRepository.save(new Post("title", "content"));

        Tag tag1 = tagRepository.save(new Tag("tag1"));
        tagRepository.saveRelationship(tag1.getId(), savedPost.getId());

        Tag tag2 = tagRepository.save(new Tag("tag2"));
        tagRepository.saveRelationship(tag2.getId(), savedPost.getId());

        List<Tag> tagsByPostId = tagRepository.findTagsByPostId(savedPost.getId());
        Assertions.assertEquals(2, tagsByPostId.size());
        Assertions.assertTrue(tagsByPostId.contains(tag1));
        Assertions.assertTrue(tagsByPostId.contains(tag2));
    }

    @Test
    public void save_shouldSaveTagWithPostAndFindById() {
        Tag savedTag = tagRepository.save(new Tag("tag1"));
        Optional<Tag> byId = tagRepository.findById(savedTag.getId());

        Assertions.assertTrue(byId.isPresent());
        Assertions.assertEquals(savedTag, byId.get());
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

        List<Tag> tags = tagRepository.findTagsByTitleIn(List.of("tag1", "tag2"));

        Assertions.assertEquals(2, tags.size());
        Assertions.assertTrue(tags.contains(tag1));
        Assertions.assertTrue(tags.contains(tag2));
    }
}
