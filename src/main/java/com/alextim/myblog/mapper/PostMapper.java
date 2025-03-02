package com.alextim.myblog.mapper;

import com.alextim.myblog.dto.NewPostDto;
import com.alextim.myblog.dto.PostDto;
import com.alextim.myblog.dto.PostShortDto;
import com.alextim.myblog.mapper.CommentMapper;
import com.alextim.myblog.model.Post;
import com.alextim.myblog.model.Tag;
import com.alextim.myblog.service.CommentService;
import com.alextim.myblog.service.TagService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {CommentMapper.class}
)
public abstract class PostMapper {

    @Autowired
    protected TagService tagService;

    @Autowired
    protected CommentService commentService;

    @Mapping(target = "tags", expression = "java(tagService.save(newPostDto.tags))")
    abstract Post toModel(NewPostDto newPostDto);

    @Mapping(target = "commentsSize", expression = "java(commentService.countByPost(post))")
    @Mapping(target = "tags", expression = "java(tagsToString(post.getTags()))")
    abstract PostShortDto toShortDto(Post post);

    @Mapping(target = "tags", expression = "java(tagsToString(post.getTags()))")
    abstract PostDto toDto(Post post);

    String tagsToString(Set<Tag> tags) {
        return tags != null ? tags.stream().map(Tag::getTitle).collect(Collectors.joining(", ")) : null;
    }
}
