package com.alextim.myblog.repository;

import com.alextim.myblog.config.AppTestConfig;
import com.alextim.myblog.model.Comment;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppTestConfig.class})
public class PostRepositoryTest {

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
    public void save_shouldSaveAndFindById() {
        Post post = new Post("title", "content", "url");
        Post savedPost = postRepository.save(post);
        Assertions.assertTrue(savedPost.getId() != 0);

        Optional<Post> byId = postRepository.findById(savedPost.getId());
        Assertions.assertTrue(byId.isPresent());
        Assertions.assertEquals("title", byId.get().getTitle());
        Assertions.assertEquals("content", byId.get().getContent());
    }

    @Test
    public void update_shouldUpdateAndFindById() {
        Post post = new Post("title", "content");
        Post savedPost = postRepository.save(post);
        Assertions.assertTrue(savedPost.getId() != 0);

        savedPost.setTitle("NewTitle");
        savedPost.setContent("NewContent");
        savedPost.setImageUrl("NewImageUrl");

        int updatedEntitiesNumber = postRepository.update(savedPost);
        Assertions.assertEquals(1, updatedEntitiesNumber);

        Optional<Post> byId = postRepository.findById(savedPost.getId());
        Assertions.assertTrue(byId.isPresent());
        Assertions.assertEquals("NewTitle", byId.get().getTitle());
        Assertions.assertEquals("NewContent", byId.get().getContent());
        Assertions.assertEquals("NewImageUrl", byId.get().getImageUrl());
    }

    @Test
    public void findById_shouldSaveAndFindPostWithTagsAndCommentsById() {
        Post post = new Post("title", "content");
        Post savedPost = postRepository.save(post);

        Comment comment1 = new Comment("comment1", savedPost.getId());
        commentRepository.save(comment1);
        Comment comment2 = new Comment("comment2", savedPost.getId());
        commentRepository.save(comment2);

        Tag tag = tagRepository.save(new Tag("tag"));
        tagRepository.saveRelationship(tag.getId(), savedPost.getId());

        Optional<Post> byId = postRepository.findById(savedPost.getId());
        Assertions.assertTrue(byId.isPresent());
        Assertions.assertEquals("title", byId.get().getTitle());
        Assertions.assertEquals("content", byId.get().getContent());

        List<Comment> comments = byId.get().getComments();
        Assertions.assertEquals(2, comments.size());
        Assertions.assertTrue(comments.contains(comment1));
        Assertions.assertTrue(comments.contains(comment2));

        Set<Tag> tags = byId.get().getTags();
        Assertions.assertEquals(1, tags.size());
        Assertions.assertTrue(tags.contains(tag));

        Assertions.assertEquals(2, byId.get().getCommentsSize());
    }

    @Test
    public void findAllPostsWithTags_shouldSaveAndFindPostWithTagsAndCommentCountById() {
        Post post1 = new Post("title1", "content1");
        Post savedPost1 = postRepository.save(post1);

        Comment comment1 = new Comment("comment1", savedPost1.getId());
        commentRepository.save(comment1);
        Comment comment2 = new Comment("comment2", savedPost1.getId());
        commentRepository.save(comment2);

        Tag tag = tagRepository.save(new Tag("tag"));
        tagRepository.saveRelationship(tag.getId(), savedPost1.getId());

        Post post2 = new Post("title2", "content2");
        Post savedPost2 = postRepository.save(post2);

        Comment comment3 = new Comment("comment3", savedPost2.getId());
        commentRepository.save(comment3);

        tagRepository.saveRelationship(tag.getId(), savedPost2.getId());

        List<Post> post = postRepository.findAllPostsWithTags(10, 0);
        Assertions.assertEquals(2, post.size());

        Assertions.assertEquals(1, post.get(0).getTags().size());
        Assertions.assertTrue( post.get(0).getTags().contains(tag));
        Assertions.assertEquals(1, post.get(1).getTags().size());
        Assertions.assertTrue( post.get(1).getTags().contains(tag));

        Assertions.assertEquals(2, post.get(0).getCommentsSize());
        Assertions.assertEquals(1, post.get(1).getCommentsSize());
    }

    @Test
    public void findByTags_shouldSaveAndFindPostWithTagsAndCommentCountByTagId() {
        Post post1 = new Post("title1", "content1");
        Post savedPost1 = postRepository.save(post1);

        Comment comment1 = new Comment("comment1", savedPost1.getId());
        commentRepository.save(comment1);
        Comment comment2 = new Comment("comment2", savedPost1.getId());
        commentRepository.save(comment2);

        Tag tag1 = tagRepository.save(new Tag("tag1"));
        tagRepository.saveRelationship(tag1.getId(), savedPost1.getId());

        Post post2 = new Post("title2", "content2");
        Post savedPost2 = postRepository.save(post2);

        Comment comment3 = new Comment("comment3", savedPost2.getId());
        commentRepository.save(comment3);

        tagRepository.saveRelationship(tag1.getId(), savedPost2.getId());

        Tag tag2 = tagRepository.save(new Tag("tag2"));

        List<Post> post = postRepository.findByTagIds(List.of(tag1.getId(), tag2.getId()), 10, 0);
        Assertions.assertEquals(2, post.size());

        Assertions.assertEquals(1, post.get(0).getTags().size());
        Assertions.assertTrue( post.get(0).getTags().contains(tag1));
        Assertions.assertEquals(1, post.get(1).getTags().size());
        Assertions.assertTrue( post.get(1).getTags().contains(tag1));

        Assertions.assertEquals(2, post.get(0).getCommentsSize());
        Assertions.assertEquals(1, post.get(1).getCommentsSize());
    }
}
