package com.alextim.myblog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewPostDto {

    public String title;

    public String content;

    public String imageUrl;

    public String tags;
}
