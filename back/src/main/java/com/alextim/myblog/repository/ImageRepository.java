package com.alextim.myblog.repository;

import com.alextim.myblog.model.Image;

import java.util.Optional;

public interface ImageRepository extends AbstractRepository<Image> {

    Image save(Image image);

    Optional<Image> findById(long id);

    Optional<Image>  findByPostId(long postId);

    int update(Image image);

    void deleteByPostId(long id);
}
