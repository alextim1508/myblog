package com.alextim.myblog.controller;

import com.alextim.myblog.model.Post;
import com.alextim.myblog.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}/likes")
@RequiredArgsConstructor
@Slf4j
public class LikeController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Integer> incrementLikes(@PathVariable Long postId) {
        log.info("Incrementing likes for post with ID: {}", postId);

        Post updatedPost = postService.like(postId);
        log.info("Successfully incremented likes for post with ID: {}. New like count: {}",
                postId, updatedPost.getLikesCount());

        return ResponseEntity.ok(updatedPost.getLikesCount());
    }
}
