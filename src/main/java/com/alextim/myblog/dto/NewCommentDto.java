package com.alextim.myblog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentDto {

    @NotNull
    @Size(min=10)
    public String content;

    @NotNull
    public Long postId;
}
