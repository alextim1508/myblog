package com.alextim.myblog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@Getter
public class UpdateCommentRequestDto {

    @NotNull(message = "{comment.id.not_null}")
    private Long id;

    @NotBlank(message = "{comment.text.not_blank}")
    @Size(max = 1000, message = "{comment.text.size_exceeded}")
    private String text;

    @NotNull(message = "{comment.postId.not_null}")
    private Long postId;
}
