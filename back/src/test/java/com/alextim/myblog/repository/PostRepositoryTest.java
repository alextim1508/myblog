package com.alextim.myblog.repository;

import com.alextim.myblog.model.Post;
import com.alextim.myblog.model.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PostRepositoryTest extends RepositoryTest{

    @Test
    public void save_shouldSaveAndFindById() {
        Post savedPost = postRepository.save(new Post("title", "content"));
        assertTrue(savedPost.getId() != 0);

        Optional<Post> byId = postRepository.findById(savedPost.getId());
        assertTrue(byId.isPresent());
        assertEquals(savedPost, byId.get());
    }

    @Test
    public void update_shouldUpdateAndFindById() {
        Post post = new Post("title", "content");
        Post savedPost = postRepository.save(post);
        assertTrue(savedPost.getId() != 0);

        savedPost.setTitle("NewTitle");
        savedPost.setText("NewContent");

        int updatedEntitiesNumber = postRepository.update(savedPost);
        assertEquals(1, updatedEntitiesNumber);

        Optional<Post> byId = postRepository.findById(savedPost.getId());
        assertTrue(byId.isPresent());
        assertEquals("NewTitle", byId.get().getTitle());
        assertEquals("NewContent", byId.get().getText());
    }


    @Test
    public void findByTagIds_shouldReturnPostsByTagIds() {
        Post savedPost1 = postRepository.save(new Post("title1", "content1"));
        Post savedPost2 = postRepository.save(new Post("title2", "content2"));

        Tag savedTag = tagRepository.save(new Tag("java"));
        tagRepository.saveRelationship(savedTag.getId(), savedPost1.getId());
        tagRepository.saveRelationship(savedTag.getId(), savedPost2.getId());

        List<Post> posts = postRepository.findByTagIds(List.of(savedTag.getId()), Integer.MAX_VALUE, 0);

        assertEquals(2, posts.size());
        assertTrue(posts.contains(savedPost1));
        assertTrue(posts.contains(savedPost2));
    }

    @Test
    public void findByTagIds_shouldReturnEmptyListWhenTagIdsEmpty() {
        List<Post> posts = postRepository.findByTagIds(Collections.emptyList(), Integer.MAX_VALUE, 0);
        assertEquals(0, posts.size());
    }

    @Test
    public void findByTagIds_shouldReturnEmptyListWhenTagIdsNotExist() {
        List<Post> posts = postRepository.findByTagIds(List.of(999L), Integer.MAX_VALUE, 0);
        assertEquals(0, posts.size());
    }

    @Test
    public void findByTagIds_shouldReturnEmptyListWhenTagHasNoPosts() {
        Tag savedTag = tagRepository.save(new Tag("empty"));

        List<Post> posts = postRepository.findByTagIds(List.of(savedTag.getId()), Integer.MAX_VALUE, 0);

        assertEquals(0, posts.size());
    }

    @Test
    public void findByTagIds_shouldReturnPostsByMultipleTagIds() {
        Post savedPost1 = postRepository.save(new Post("title1", "content1"));
        Post savedPost2 = postRepository.save(new Post("title2", "content2"));

        Tag savedTag1 = tagRepository.save(new Tag("java"));
        Tag savedTag2 = tagRepository.save(new Tag("spring"));

        tagRepository.saveRelationship(savedTag1.getId(), savedPost1.getId());
        tagRepository.saveRelationship(savedTag2.getId(), savedPost2.getId());

        List<Post> posts = postRepository.findByTagIds(List.of(savedTag1.getId(), savedTag2.getId()), Integer.MAX_VALUE, 0);

        assertEquals(2, posts.size());
        assertTrue(posts.contains(savedPost1));
        assertTrue(posts.contains(savedPost2));
    }

    @Test
    public void findByTagIds_shouldRespectLimitAndOffset() {
        Post savedPost1 = postRepository.save(new Post("title1", "content1"));
        Post savedPost2 = postRepository.save(new Post("title2", "content2"));
        Post savedPost3 = postRepository.save(new Post("title3", "content3"));

        Tag savedTag = tagRepository.save(new Tag("java"));

        tagRepository.saveRelationship(savedTag.getId(), savedPost1.getId());
        tagRepository.saveRelationship(savedTag.getId(), savedPost2.getId());
        tagRepository.saveRelationship(savedTag.getId(), savedPost3.getId());

        List<Post> posts = postRepository.findByTagIds(List.of(savedTag.getId()), 2, 0);

        assertEquals(2, posts.size());
        assertEquals(savedPost1, posts.get(0));
        assertEquals(savedPost2, posts.get(1));
    }

    @Test
    public void findByTagIds_shouldRespectOffset() {
        Post savedPost1 = postRepository.save(new Post("title1", "content1"));
        Post savedPost2 = postRepository.save(new Post("title2", "content2"));
        Post savedPost3 = postRepository.save(new Post("title3", "content3"));

        Tag savedTag = tagRepository.save(new Tag("java"));

        tagRepository.saveRelationship(savedTag.getId(), savedPost1.getId());
        tagRepository.saveRelationship(savedTag.getId(), savedPost2.getId());
        tagRepository.saveRelationship(savedTag.getId(), savedPost3.getId());

        List<Post> posts = postRepository.findByTagIds(List.of(savedTag.getId()), 1, 1);

        assertEquals(1, posts.size());
        assertEquals(savedPost2, posts.get(0));
    }

    @Test
    public void findByTitleOrContent_shouldReturnPostsByTitle() {
        Post savedPost1 = postRepository.save(new Post("Java Guide", "Learn Java"));
        Post savedPost2 = postRepository.save(new Post("Spring Boot", "Learn Spring"));

        List<Post> posts = postRepository.findByTitleOrContent("java", Integer.MAX_VALUE, 0);

        assertEquals(1, posts.size());
        assertEquals(savedPost1, posts.get(0));
    }

    @Test
    public void findByTitleOrContent_shouldReturnPostsByContent() {
        Post savedPost1 = postRepository.save(new Post("Guide", "Learn Java here"));
        Post savedPost2 = postRepository.save(new Post("Article", "Learn Kotlin here"));

        List<Post> posts = postRepository.findByTitleOrContent("java", Integer.MAX_VALUE, 0);

        assertEquals(1, posts.size());
        assertEquals(savedPost1, posts.get(0));
    }

    @Test
    public void findByTitleOrContent_shouldReturnPostsByTitleAndContent() {
        Post savedPost1 = postRepository.save(new Post("Java Guide", "Learn Java"));
        Post savedPost2 = postRepository.save(new Post("Article", "Learn Kotlin"));
        Post savedPost3 = postRepository.save(new Post("Kotlin", "Learn Kotlin here"));

        List<Post> posts = postRepository.findByTitleOrContent("kotlin", Integer.MAX_VALUE, 0);

        assertEquals(2, posts.size());
        assertTrue(posts.contains(savedPost2));
        assertTrue(posts.contains(savedPost3));
    }

    @Test
    public void findByTitleOrContent_shouldReturnEmptyListWhenQueryIsNull() {
        List<Post> posts = postRepository.findByTitleOrContent(null, Integer.MAX_VALUE, 0);

        assertEquals(0, posts.size());
    }

    @Test
    public void findByTitleOrContent_shouldReturnEmptyListWhenQueryIsEmpty() {
        List<Post> posts = postRepository.findByTitleOrContent("", Integer.MAX_VALUE, 0);

        assertEquals(0, posts.size());
    }

    @Test
    public void findByTitleOrContent_shouldReturnEmptyListWhenQueryIsWhitespace() {
        List<Post> posts = postRepository.findByTitleOrContent("   ", Integer.MAX_VALUE, 0);

        assertEquals(0, posts.size());
    }

    @Test
    public void findByTitleOrContent_shouldReturnEmptyListWhenNoMatch() {
        postRepository.save(new Post("title1", "content1"));

        List<Post> posts = postRepository.findByTitleOrContent("nonexistent", Integer.MAX_VALUE, 0);

        assertEquals(0, posts.size());
    }

    @Test
    public void findByTitleOrContent_shouldRespectLimitAndOffset() {
        postRepository.save(new Post("Java Guide", "Learn Java"));
        postRepository.save(new Post("Java Tips", "More Java"));
        postRepository.save(new Post("Kotlin", "Learn Kotlin"));

        List<Post> posts = postRepository.findByTitleOrContent("java", 1, 0); // limit = 1, offset = 0

        assertEquals(1, posts.size());
    }

    @Test
    public void findByTitleOrContent_shouldRespectOffset() {
        postRepository.save(new Post("Java Guide", "Learn Java"));
        postRepository.save(new Post("Java Tips", "More Java"));
        postRepository.save(new Post("Java Advanced", "Deep Java"));

        List<Post> posts = postRepository.findByTitleOrContent("java", 1, 1); // limit = 1, offset = 1

        assertEquals(1, posts.size());
        assertEquals("Java Tips", posts.get(0).getTitle());
    }
}
