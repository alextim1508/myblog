package com.alextim.myblog.controller;

import com.alextim.myblog.dto.NewPostDto;
import com.alextim.myblog.dto.PostDto;
import com.alextim.myblog.dto.PostShortDto;
import com.alextim.myblog.mapper.PostMapper;
import com.alextim.myblog.model.Post;
import com.alextim.myblog.model.Tag;
import com.alextim.myblog.service.PostService;
import com.alextim.myblog.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Supplier;

@Slf4j
@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;

    private final TagService tagService;

    @PostMapping
    public String save(@Valid @ModelAttribute NewPostDto newPostDto, BindingResult bindingResult) {
        log.info("save post: {}", newPostDto);

        if(bindingResult.hasErrors()) {
            throw new RuntimeException(bindingResult.getAllErrors().toString());
        }

        Post post = postMapper.toModel(newPostDto);
        postService.save(post);

        return "redirect:/post";
    }

    @PostMapping(value = "/{id}", params = "_method=put")
    public String edit(@PathVariable("id") Long id,
                       @Valid @ModelAttribute NewPostDto newPostDto,
                       BindingResult bindingResult) {
        log.info("update post with id {}: {}", id, newPostDto);

        if(bindingResult.hasErrors()) {
            throw new RuntimeException(bindingResult.getAllErrors().toString());
        }

        Post post = postMapper.toModel(newPostDto);
        post.setId(id);
        postService.save(post);

        return "redirect:/post";
    }

    @GetMapping
    public String getPosts(@RequestParam(name = "tag", required = false) String tagTitle,
                           @RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "size", defaultValue = "10") int size,
                           Model model) {
        log.info("get post with page {} size {} by tag '{}'", page, size, tagTitle);

        List<Post> posts;
        if (tagTitle != null && !tagTitle.isEmpty()) {
            posts = tagService.findTagByTitle(tagTitle)
                    .map(value -> postService.findByTag(value, page, size).getContent())
                    .orElseGet(Collections::emptyList);
        } else {
            posts = postService.findAll(page, size).getContent();
        }

        List<PostShortDto> dtos = posts.stream().map(postMapper::toShortDto).toList();

        model.addAttribute("postlist", dtos);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);

        if(tagTitle != null)
            model.addAttribute("tag", tagTitle);

        return "list-posts";
    }

    @GetMapping("/{id}")
    public String getPost(@PathVariable("id") Long id, Model model) {
        log.info("get post with id {}: ", id);

        Post post = postService.findById(id);
        if (post == null) {
            return "error";
        }

        PostDto postDto = postMapper.toDto(post);

        model.addAttribute("post", postDto);
        model.addAttribute("comments", postDto.getComments());

        return "post";
    }

    @PostMapping(value = "/{id}", params = "_method=delete")
    public String delete(@PathVariable(name = "id") Long id) {
        log.info("delete post with id {}", id);

        postService.delete(id);

        return "redirect:/post";
    }

    @PostMapping("/{id}/like")
    public String likePost(@PathVariable("id") Long id) {
        log.info("like post with id {}", id);

        postService.like(id);

        return "redirect:/post/" + id;
    }
}
