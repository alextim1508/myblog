package com.alextim.myblog.mapper;

import com.alextim.myblog.dto.CreatePostRequestDto;
import com.alextim.myblog.dto.PostResponseDto;
import com.alextim.myblog.dto.UpdatePostRequestDto;
import com.alextim.myblog.model.Post;
import com.alextim.myblog.model.Tag;
import com.alextim.myblog.service.CommentService;
import com.alextim.myblog.service.TagService;
import com.alextim.myblog.service.TagServiceImpl;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",
        imports = {TagServiceImpl.class, Tag.class}
)
public abstract class PostMapper {

    @Autowired
    protected TagService tagService;

    @Autowired
    protected CommentService commentService;

    @Mapping(target = "commentsCount", expression = "java(commentService.countByPostId(post.getId()))")
    @Mapping(target = "tags", expression = "java(tagService.findTagsByPostId(post.getId()).stream().map(Tag::getTitle).toList())")
    public abstract PostResponseDto toDto(Post post);

    public abstract Post toModel(CreatePostRequestDto dto);

    public abstract Post toModel(UpdatePostRequestDto dto);

    @AfterMapping
    public void saveTags(CreatePostRequestDto dto) {
        tagService.save(dto.getTags());
    }

    @AfterMapping
    public void saveTags(UpdatePostRequestDto dto) {
        tagService.save(dto.getTags());
    }
}
