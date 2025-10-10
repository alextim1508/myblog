package com.alextim.myblog.controller;

import com.alextim.myblog.dto.NewCommentDto;
import com.alextim.myblog.mapper.CommentMapper;
import com.alextim.myblog.model.Comment;
import com.alextim.myblog.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PostMapping
    public String save(@Valid @ModelAttribute NewCommentDto newCommentDto) {
        log.info("save comment: {}", newCommentDto);

        Comment comment = commentMapper.toModel(newCommentDto);
        commentService.save(comment);

        return "redirect:/post/" + newCommentDto.getPostId();
    }

    @PostMapping("/{id}")
    public String editComment(@PathVariable("id") Long id,
                              @Valid @ModelAttribute NewCommentDto newCommentDto) {
        log.info("update comment with id {}: {}", id, newCommentDto);

        Comment comment = commentMapper.toModel(newCommentDto);
        comment.setId(id);
        commentService.save(comment);

        return "redirect:/post/" + newCommentDto.getPostId();
    }

    @PostMapping(value = "/{id}", params = "_method=delete")
    public String deleteComment(@PathVariable("id") Long id) {
        log.info("delete comment with id {}", id);

        commentService.deleteById(id);

        return "redirect:/post";
    }
}
