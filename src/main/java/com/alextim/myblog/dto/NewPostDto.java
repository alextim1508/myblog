package com.alextim.myblog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewPostDto {

    public String title;

    public String content;

    public String tags;
}
