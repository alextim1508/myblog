package com.alextim.myblog.controller;

import com.alextim.myblog.config.AppTestConfig;
import com.alextim.myblog.model.Comment;
import com.alextim.myblog.model.Post;
import com.alextim.myblog.model.Tag;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppTestConfig.class})
@Transactional
class PostControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PostService postService;

    @Autowired
    private TagService tagService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void save_shouldSavePostAndReturnHtmlWithAllPosts() throws Exception {
        mockMvc.perform(post("/post")
                        .param("title", "title")
                        .param("content", "content")
                        .param("tags", "tag1, tag2")
                        .param("imageUrl", "imageUrl"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post"));

        List<Post> posts = postService.findAll(0, 10).getContent();
        Assertions.assertEquals(1, posts.size());

        Assertions.assertEquals("title", posts.get(0).getTitle());
        Assertions.assertEquals("content", posts.get(0).getContent());
        Assertions.assertEquals("imageUrl", posts.get(0).getImageUrl());
        Assertions.assertEquals(2, posts.get(0).getTags().size());
        Assertions.assertTrue(posts.get(0).getTags().contains(new Tag("tag1")));
    }

    @Test
    void update_shouldUpdatePostAndReturnHtmlWithAllPosts() throws Exception {
        mockMvc.perform(post("/post")
                        .param("title", "title")
                        .param("content", "content")
                        .param("tags", "tag1, tag2")
                        .param("imageUrl", "imageUrl"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post"));

        List<Post> posts = postService.findAll(0, 10).getContent();
        Assertions.assertEquals(1, posts.size());
        Assertions.assertEquals("title", posts.get(0).getTitle());
        Assertions.assertEquals("content", posts.get(0).getContent());
        Assertions.assertEquals("imageUrl", posts.get(0).getImageUrl());
        Assertions.assertEquals(2, posts.get(0).getTags().size());
        Assertions.assertTrue(posts.get(0).getTags().contains(new Tag("tag1")));
    }

    @Test
    void getPosts_shouldReturnHtmlWithPosts() throws Exception {
        postService.save(new Post("title1", "content1", "url1"));
        postService.save(new Post("title2", "content2", "url2"));
        postService.save(new Post("title3", "content3", "url3"));

        mockMvc.perform(get("/post"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("list-posts"))
                .andExpect(model().attributeExists("postlist"))
                .andExpect(xpath("//table/tbody/tr").nodeCount(3))
                .andExpect(xpath("//table/tbody/tr[1]/td[1]").string("title1"))
                .andExpect(xpath("//table/tbody/tr[1]/td[3]").string("content1"))
                .andExpect(xpath("//table/tbody/tr[2]/td[1]").string("title2"))
                .andExpect(xpath("//table/tbody/tr[2]/td[3]").string("content2"))
                .andExpect(xpath("//table/tbody/tr[3]/td[1]").string("title3"))
                .andExpect(xpath("//table/tbody/tr[3]/td[3]").string("content3"));
    }

    @Test
    void getPost_shouldReturnHtmlWithPostsByTag() throws Exception {
        Tag tag1 = tagService.save(new Tag("tag1"));
        Tag tag2 = tagService.save(new Tag("tag2"));

        Post post1 = new Post("title1", "content1");
        tag1.addPost(post1);
        tag2.addPost(post1);
        postService.save(post1);

        Post post2 = new Post("title2", "content2");
        tag1.addPost(post2);
        postService.save(post2);

        mockMvc.perform(get("/post").param("tag", "tag1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("list-posts"))
                .andExpect(model().attributeExists("postlist"))
                .andExpect(xpath("//table/tbody/tr").nodeCount(2))
                .andExpect(xpath("//table/tbody/tr[1]/td[1]").string("title1"))
                .andExpect(xpath("//table/tbody/tr[1]/td[3]").string("content1"))
                .andExpect(xpath("//table/tbody/tr[2]/td[1]").string("title2"))
                .andExpect(xpath("//table/tbody/tr[2]/td[3]").string("content2"));
    }

    @Test
    void getPost_shouldReturnHtmlWithPostById() throws Exception {
        Tag tag1 = tagService.save(new Tag("tag1"));
        Tag tag2 = tagService.save(new Tag("tag2"));

        Post post = new Post("title", "content");
        post.getComments().add(new Comment("comment1", post));
        post.getComments().add(new Comment("comment2", post));
        tag1.addPost(post);
        tag2.addPost(post);

        Post savedPost = postService.save(post);

        mockMvc.perform(get("/post/" + savedPost.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("comments"));
    }

    @Test
    void delete_shouldRemovePostFromDatabaseAndRedirect() throws Exception {
        Post savedPost = postService.save(new Post("title1", "content1"));

        mockMvc.perform(post("/post/" + savedPost
                        .getId()).param("_method", "delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post"));

    }
}
