package com.alextim.myblog.controller;

import com.alextim.myblog.dto.CreatePostRequestDto;
import com.alextim.myblog.dto.PostListResponseDto;
import com.alextim.myblog.dto.PostResponseDto;
import com.alextim.myblog.dto.UpdatePostRequestDto;
import com.alextim.myblog.mapper.PostMapper;
import com.alextim.myblog.model.Post;
import com.alextim.myblog.service.PostService;
import com.alextim.myblog.service.TagService;
import com.alextim.myblog.util.PaginationHelper;
import com.alextim.myblog.util.PaginationResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private TagService tagService;

    @MockitoBean
    private PostMapper postMapper;

    @Test
    void createPost_shouldReturnCreatedPost() throws Exception {
        CreatePostRequestDto request = new CreatePostRequestDto("Test Title", "Test Content", List.of("java", "spring"));

        Post post = new Post("Test Title", "Test Content");
        post.setId(1L);

        PostResponseDto responseDto = new PostResponseDto(
                1L,
                "Test Title",
                "Test Content",
                List.of("java", "spring"),
                0,
                0
        );

        when(postMapper.toModel(any(CreatePostRequestDto.class))).thenReturn(post);
        when(postService.save(any())).thenReturn(post);
        when(postMapper.toDto(any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Test Title",
                                    "text": "Test Content",
                                    "tags": ["java", "spring"]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags[0]").value("java"))
                .andExpect(jsonPath("$.tags[1]").value("spring"))
                .andExpect(jsonPath("$.likesCount").value(0))
                .andExpect(jsonPath("$.commentsCount").value(0));

        verify(postService).save(any(Post.class));
    }

    @Test
    void updatePost_shouldReturnUpdatedPost() throws Exception {
        Long postId = 1L;
        UpdatePostRequestDto request = new UpdatePostRequestDto(postId, "Updated Title", "Updated Content", List.of("java", "spring"));

        Post post = new Post("Updated Title", "Updated Content");
        post.setId(postId);

        PostResponseDto responseDto = new PostResponseDto(
                postId,
                "Updated Title",
                "Updated Content",
                List.of("java", "spring"),
                5,
                3
        );

        when(postMapper.toModel(any(UpdatePostRequestDto.class))).thenReturn(post);
        when(postService.save(any())).thenReturn(post);
        when(postMapper.toDto(any())).thenReturn(responseDto);

        mockMvc.perform(put("/api/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": 1,
                                    "title": "Updated Title",
                                    "text": "Updated Content",
                                    "tags": ["java", "spring"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags[0]").value("java"))
                        .andExpect(jsonPath("$.tags[1]").value("spring"))
                        .andExpect(jsonPath("$.likesCount").value(5))
                        .andExpect(jsonPath("$.commentsCount").value(3));

        verify(postService).save(any(Post.class));
    }

    @Test
    void getPosts_shouldReturnPostList() throws Exception {
        int page = 1;
        int size = 10;
        String search = "java";

        Post post1 = new Post("Test Post 1", "Content 1");
        post1.setId(1L);

        Post post2 = new Post("Test Post 2", "Content 2");
        post2.setId(2L);

        PostResponseDto responseDto1 = new PostResponseDto(
                1L,
                "Test Post 1",
                "Content 1",
                List.of("java"),
                10,
                5
        );

        PostResponseDto responseDto2 = new PostResponseDto(
                2L,
                "Test Post 2",
                "Content 2",
                List.of("spring"),
                20,
                8
        );

        when(postService.getPosts(eq(search), eq(page), eq(size))).thenReturn(List.of(post1, post2));
        when(postService.count()).thenReturn(20L);
        when(postMapper.toDto(post1)).thenReturn(responseDto1);
        when(postMapper.toDto(post2)).thenReturn(responseDto2);

        PaginationResult pagination = PaginationHelper.calculate(20L, size, page);

        mockMvc.perform(get("/api/posts")
                        .param("search", search)
                        .param("pageNumber", String.valueOf(page))
                        .param("pageSize", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts[0].id").value(1L))
                .andExpect(jsonPath("$.posts[0].title").value("Test Post 1"))
                .andExpect(jsonPath("$.posts[0].tags[0]").value("java"))
                .andExpect(jsonPath("$.posts[0].likesCount").value(10))
                .andExpect(jsonPath("$.posts[1].id").value(2L))
                .andExpect(jsonPath("$.posts[1].title").value("Test Post 2"))
                .andExpect(jsonPath("$.posts[1].tags[0]").value("spring"))
                .andExpect(jsonPath("$.posts[1].likesCount").value(20))
                .andExpect(jsonPath("$.hasPrev").value(pagination.isHasPrev()))
                .andExpect(jsonPath("$.hasNext").value(pagination.isHasNext()))
                .andExpect(jsonPath("$.lastPage").value(pagination.getLastPage()));

        verify(postService).getPosts(eq(search), eq(page), eq(size));
        verify(postService).count();
    }

    @Test
    void getPost_shouldReturnPostById() throws Exception {
        Long postId = 1L;

        Post post = new Post("Test Post", "Content");
        post.setId(postId);

        PostResponseDto responseDto = new PostResponseDto(
                postId,
                "Test Post",
                "Content",
                List.of("java", "spring"),
                15,
                7
        );

        when(postService.findById(postId)).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(responseDto);

        mockMvc.perform(get("/api/posts/{id}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags[0]").value("java"))
                .andExpect(jsonPath("$.tags[1]").value("spring"))
                .andExpect(jsonPath("$.likesCount").value(15))
                .andExpect(jsonPath("$.commentsCount").value(7));

        verify(postService).findById(postId);
    }

    @Test
    void deletePost_shouldDeletePost() throws Exception {
        Long postId = 1L;

        mockMvc.perform(delete("/api/posts/{id}", postId))
                .andExpect(status().isOk());

        verify(postService).delete(postId);
    }
}