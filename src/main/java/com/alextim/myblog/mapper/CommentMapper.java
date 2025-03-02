package com.alextim.myblog.mapper;

import com.alextim.myblog.dto.CommentDto;
import com.alextim.myblog.dto.NewCommentDto;
import com.alextim.myblog.model.Comment;
import com.alextim.myblog.service.PostService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {

    @Autowired
    protected PostService postService;

    public abstract CommentDto toDto(Comment comment);

    public abstract List<CommentDto> toDto(List<Comment> comments);

    @Mapping(target = "post", expression = "java(postService.findById(newCommentDto.getPostId()))")
    public abstract Comment toModel(NewCommentDto newCommentDto);
}
