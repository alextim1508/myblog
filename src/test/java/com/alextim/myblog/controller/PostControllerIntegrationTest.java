package com.alextim.myblog.controller;

import com.alextim.myblog.config.AppTestConfig;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppTestConfig.class})
class PostControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PostService postService;

    @Autowired
    private TagService tagService;

    @Autowired
    private CommentService commentService;

    private MockMvc mockMvc;

    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        commentRepository.delete();
        postRepository.delete();
        tagRepository.deleteRelationships();
        tagRepository.delete();
    }

    @Test
    void save_shouldSavePostAndReturnHtmlWithAllPosts() throws Exception {
        mockMvc.perform(post("/post")
                        .param("title", "titletitletitle")
                        .param("content", "contentcontentcontentcontentcontentcontent")
                        .param("tags", "tag1, tag2")
                        .param("imageUrl", "imageUrl"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post"));

        List<Post> posts = postService.findAll(0, 10);
        Assertions.assertEquals(1, posts.size());

        Assertions.assertEquals("titletitletitle", posts.get(0).getTitle());
        Assertions.assertEquals("contentcontentcontentcontentcontentcontent", posts.get(0).getContent());
        Assertions.assertEquals("imageUrl", posts.get(0).getImageUrl());
        Assertions.assertEquals(2, posts.get(0).getTags().size());
        Assertions.assertTrue(posts.get(0).getTags().contains(new Tag("tag1")));
    }

    @Test
    void update_shouldUpdatePostAndReturnHtmlWithAllPosts() throws Exception {
        mockMvc.perform(post("/post")
                        .param("title", "titletitletitle")
                        .param("content", "contentcontentcontentcontentcontentcontent")
                        .param("tags", "tag1, tag2")
                        .param("imageUrl", "imageUrl"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post"));

        List<Post> posts = postService.findAll(0, 10);
        Assertions.assertEquals(1, posts.size());
        Assertions.assertEquals("titletitletitle", posts.get(0).getTitle());
        Assertions.assertEquals("contentcontentcontentcontentcontentcontent", posts.get(0).getContent());
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
        Post savedPost1 = postService.save(new Post("title1", "content1"));
        Tag savedTag1 = tagService.save(new Tag("tag1"), savedPost1.getId());
        tagService.save(new Tag("tag2"), savedPost1.getId());

        Post savedPost2 = postService.save(new Post("title2", "content2"));
        tagService.save(savedTag1, savedPost2.getId());

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
        Post savedPost1 = postService.save(new Post("title1", "content1"));

        tagService.save(new Tag("tag1"), savedPost1.getId());
        tagService.save(new Tag("tag2"), savedPost1.getId());

        commentService.save(new Comment("comment1", savedPost1.getId()));
        commentService.save(new Comment("comment2", savedPost1.getId()));

        mockMvc.perform(get("/post/" + savedPost1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("comments"));
    }

    @Test
    void delete_shouldRemovePostFromDatabaseAndRedirect() throws Exception {
        Post savedPost = postService.save(new Post("title1", "content1"));

        tagService.save(new Tag("tag1"), savedPost.getId());
        tagService.save(new Tag("tag2"), savedPost.getId());

        commentService.save(new Comment("comment1", savedPost.getId()));
        commentService.save(new Comment("comment2", savedPost.getId()));

        mockMvc.perform(post("/post/" + savedPost
                        .getId()).param("_method", "delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post"));

        List<Tag> tags = tagService.findAll();
        Assertions.assertEquals(2, tags.size());

        List<Comment> comments = commentService.findAll(0, 10);
        Assertions.assertEquals(0, comments.size());
    }
}
