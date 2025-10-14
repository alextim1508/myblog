package com.alextim.myblog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@Getter
public class UpdatePostRequestDto {

    @NotNull(message = "{post.id.not_null}")
    private Long id;

    @NotBlank(message = "{post.title.not_blank}")
    @Size(max = 256, message = "{post.title.size_exceeded}")
    private String title;

    @NotBlank(message = "{post.text.not_blank}")
    private String text;

    @NotNull(message = "{post.tags.not_null}")
    private List<String> tags;
}
