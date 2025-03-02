package com.alextim.myblog.service;

import com.alextim.myblog.config.AppTestConfig;
import com.alextim.myblog.model.Post;
import com.alextim.myblog.model.Tag;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppTestConfig.class})
@Transactional
public class TagServiceIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TagService tagService;

    @Autowired
    private PostService postService;

    @Test
    public void save_shouldSaveTag() {
        Tag tag = new Tag("tag");

        Tag savedTag = tagService.save(tag);

        List<Tag> tags = tagService.findAll();

        Assertions.assertEquals(1, tags.size());
        Assertions.assertEquals(tag, tags.get(0));
        Assertions.assertEquals(savedTag.getId(), tags.get(0).getId());
    }

    @Test
    public void findTagByTitle_shouldFindByTitleTag() {
        Tag tag1 = new Tag("tag1");
        tagService.save(tag1);

        Tag tag2 = new Tag("tag2");
        tagService.save(tag2);

        Tag tag3 = new Tag("tag3");
        tagService.save(tag3);

        Tag tagById = tagService.findTagByTitle("tag1").get();

        Assertions.assertEquals(tagById, tag1);
    }

    @Test
    public void findTagsByTitles_shouldFindByTitlesTag() {
        Tag tag1 = new Tag("tag1");
        tagService.save(tag1);

        Tag tag2 = new Tag("tag2");
        tagService.save(tag2);

        Tag tag3 = new Tag("tag3");
        tagService.save(tag3);

        Set<Tag> tagsByTitles = tagService.findTagsByTitles(Set.of("tag1", "tag2", "tag100"));

        Assertions.assertEquals(2, tagsByTitles.size());
        Assertions.assertTrue(tagsByTitles.contains(tag1));
        Assertions.assertTrue(tagsByTitles.contains(tag2));
    }


    @Test
    public void save_shouldSaveByStringContent() {
        Tag tag1 = new Tag("tag1");
        tagService.save(tag1);

        Set<Tag> savedTags = tagService.save("tag1,tag2, tag3, ");

        Assertions.assertEquals(3, savedTags.size());
        Assertions.assertTrue(savedTags.contains(new Tag("tag1")));
        Assertions.assertTrue(savedTags.contains(new Tag("tag2")));
        Assertions.assertTrue(savedTags.contains(new Tag("tag3")));
    }

    @Test
    public void save_shouldSaveTagWithPost() {
        Post post = new Post("title", "content");
        postService.save(post);

        Tag tag1 = new Tag("tag1");
        tag1.addPost(post);

        tagService.save(tag1);

        clearHibernateCache();

        List<Tag> tags = tagService.findAll();

        Assertions.assertEquals(1, tags.get(0).getPosts().size());
        Assertions.assertTrue(tags.get(0).getPosts().contains(post));
    }

    void clearHibernateCache() {
        entityManager.flush();
        entityManager.clear();
    }
}
