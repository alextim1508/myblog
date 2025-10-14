package com.alextim.myblog.mapper;

import com.alextim.myblog.dto.CommentResponseDto;
import com.alextim.myblog.dto.CreateCommentRequestDto;
import com.alextim.myblog.dto.UpdateCommentRequestDto;
import com.alextim.myblog.model.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {

    public abstract CommentResponseDto toDto(Comment comment);

    public abstract Comment toModel(CreateCommentRequestDto dto);

    public abstract Comment toModel(UpdateCommentRequestDto dto);
}
