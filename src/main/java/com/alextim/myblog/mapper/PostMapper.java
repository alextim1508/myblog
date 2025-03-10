package com.alextim.myblog.mapper;

import com.alextim.myblog.dto.NewPostDto;
import com.alextim.myblog.dto.PostDto;
import com.alextim.myblog.dto.PostShortDto;
import com.alextim.myblog.mapper.CommentMapper;
import com.alextim.myblog.model.Post;
import com.alextim.myblog.model.Tag;
import com.alextim.myblog.service.CommentService;
import com.alextim.myblog.service.TagService;
import com.alextim.myblog.service.TagServiceImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {CommentMapper.class},
        imports = {TagServiceImpl.class}
)
public abstract class PostMapper {

    @Autowired
    protected TagService tagService;

    @Autowired
    protected CommentService commentService;

    @Mapping(target = "tags", expression = "java(tagService.save(newPostDto.tags))")
    public abstract Post toModel(NewPostDto newPostDto);

    @Mapping(target = "tags", expression = "java(TagServiceImpl.tagsToString(post.getTags()))")
    public abstract PostShortDto toShortDto(Post post);

    @Mapping(target = "tags", expression = "java(TagServiceImpl.tagsToString(post.getTags()))")
    public abstract PostDto toDto(Post post);
}
