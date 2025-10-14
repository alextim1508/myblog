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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final TagService tagService;
    private final PostMapper postMapper;

    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(@Valid @RequestBody CreatePostRequestDto request) {
        log.info("Creating post: {}", request);

        Post post = postMapper.toModel(request);
        log.debug("Mapped CreatePostRequestDto to Post model: {}", post);

        Post saved = postService.save(post);
        log.info("Successfully created post with ID: {}", saved.getId());

        PostResponseDto postResponseDto = postMapper.toDto(saved);
        log.debug("Returning post DTO: {}", postResponseDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(postResponseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long id,
                                                      @Valid @RequestBody UpdatePostRequestDto request) {
        log.info("Updating post with ID: {}", id);

        Post post = postMapper.toModel(request);
        log.debug("Mapped UpdatePostRequestDto to Post model: {}", post);

        Post updated = postService.save(post);
        log.info("Successfully updated post with ID: {}", id);

        PostResponseDto postResponseDto = postMapper.toDto(updated);
        log.debug("Returning post DTO: {}", postResponseDto);

        return ResponseEntity.ok(postResponseDto);
    }

    @GetMapping
    public ResponseEntity<PostListResponseDto> getPosts(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "pageNumber", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int size) {

        log.info("Getting posts with page {} size {} by search query '{}'", page, size, search);

        List<Post> posts = postService.getPosts(search,page, size);

        long count = postService.count();

        PaginationResult pagination = PaginationHelper.calculate(count, size, page);

        PostListResponseDto postListResponseDto = new PostListResponseDto(
                posts.stream().map(postMapper::toDto).toList(),
                pagination.isHasPrev(),
                pagination.isHasNext(),
                pagination.getLastPage()
        );

        log.debug("Returning {} posts, hasPrev: {}, hasNext: {}, lastPage: {}",
                posts.size(), pagination.isHasPrev(), pagination.isHasNext(), pagination.getLastPage());

        return ResponseEntity.ok(postListResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable("id") Long id) {
        log.info("Getting post with ID: {}", id);

        Post post = postService.findById(id);

        log.debug("Found post: {}", post);

        PostResponseDto postResponseDto = postMapper.toDto(post);
        log.debug("Returning post DTO: {}", postResponseDto);

        return ResponseEntity.ok(postResponseDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePost(@PathVariable Long id) {
        log.info("Deleting post with ID: {}", id);

        postService.delete(id);
        log.info("Successfully deleted post with ID: {}", id);
    }
}