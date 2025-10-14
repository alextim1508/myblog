package com.alextim.myblog.controller;

import com.alextim.myblog.dto.CommentResponseDto;
import com.alextim.myblog.dto.CreateCommentRequestDto;
import com.alextim.myblog.dto.UpdateCommentRequestDto;
import com.alextim.myblog.mapper.CommentMapper;
import com.alextim.myblog.model.Comment;
import com.alextim.myblog.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long postId,
                                                            @Valid @RequestBody CreateCommentRequestDto request) {
        log.info("Creating comment for post ID: {}", postId);
        log.debug("Request data: {}", request);

        Comment comment = commentMapper.toModel(request);

        Comment saved = commentService.save(comment);
        log.info("Successfully created comment with ID: {}", saved.getId());

        CommentResponseDto responseDto = commentMapper.toDto(saved);
        log.debug("Response data: {}", responseDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long postId,
                                                            @PathVariable Long commentId,
                                                            @Valid @RequestBody UpdateCommentRequestDto request) {
        log.info("Updating comment with ID: {} for post ID: {}", commentId, postId);
        log.debug("Request data: {}", request);

        Comment comment = commentMapper.toModel(request);

        Comment updated = commentService.save(comment);
        log.info("Successfully updated comment with ID: {}", commentId);

        CommentResponseDto responseDto = commentMapper.toDto(updated);
        log.debug("Response data: {}", responseDto);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable Long postId) {
        log.info("Getting all comments for post ID: {}", postId);

        List<Comment> all = commentService.findByPostId(postId);

        List<CommentResponseDto> commentResponseDtos = all.stream()
                .map(commentMapper::toDto)
                .toList();
        log.debug("Found {} comments for post ID: {}", commentResponseDtos.size(), postId);

        return ResponseEntity.ok(commentResponseDtos);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> getComment(@PathVariable Long postId,
                                                         @PathVariable Long commentId) {
        log.info("Getting comment with ID: {} for post ID: {}", commentId, postId);

        Comment comment = commentService.findById(commentId);

        CommentResponseDto responseDto = commentMapper.toDto(comment);
        log.debug("Found comment: {}", responseDto);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteComment(@PathVariable Long postId,
                              @PathVariable Long commentId) {
        log.info("Deleting comment with ID: {} for post ID: {}", commentId, postId);
        commentService.deleteById(commentId);
        log.info("Successfully deleted comment with ID: {}", commentId);
    }
}
