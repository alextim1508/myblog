package com.alextim.myblog.model;

import lombok.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"title"})
public class Tag {

    private Long id;

    @NonNull
    private String title;
}
