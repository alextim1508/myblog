package com.alextim.myblog.model;


import lombok.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"fileName", "postId"})
public class Image {

    private Long id;

    @NonNull
    private String fileName;

    @NonNull
    private byte[] data;

    @NonNull
    private Long size;

    @NonNull
    private Long postId;
}
