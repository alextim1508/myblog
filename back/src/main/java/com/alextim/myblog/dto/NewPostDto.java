package com.alextim.myblog.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewPostDto {

    @NotNull
    @Size(min=5)
    public String title;

    @NotNull
    @Size(min=10)
    public String content;

    public String imageUrl;

    public String tags;
}
