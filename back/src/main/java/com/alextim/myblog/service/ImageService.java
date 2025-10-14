package com.alextim.myblog.service;

import com.alextim.myblog.model.Image;

import java.util.Optional;

public interface ImageService {
    Image save(Image image);

    Optional<Image> findByPostId(long postId);
}
