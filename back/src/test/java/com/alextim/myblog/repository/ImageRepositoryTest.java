package com.alextim.myblog.repository;

import com.alextim.myblog.model.Image;
import com.alextim.myblog.model.Post;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;


public class ImageRepositoryTest extends RepositoryTest{

    @Test
    public void save_shouldSaveAndFindItById() {
        Post savedPost = postRepository.save(new Post("title", "content"));

        Image savedImage = imageRepository.save(new Image("file", new byte [] {
            0x1, 0x2, 0x3, 0x4, 0x5 }, 5L, savedPost.getId()));

        Optional<Image> byId = imageRepository.findById(savedImage.getId());
        Assertions.assertTrue(byId.isPresent());
        Assertions.assertEquals(savedImage, byId.get());
    }

    @Test
    public void update_shouldUpdateAndFindItById() {
        Post savedPost = postRepository.save(new Post("title", "content"));

        Image savedImage = imageRepository.save(new Image("file", new byte [] {
                0x1, 0x2, 0x3, 0x4, 0x5 }, 5L, savedPost.getId()));

        savedImage.setFileName("NewFileName");
        int count = imageRepository.update(savedImage);

        Assertions.assertEquals(1, count);

        Optional<Image> byId = imageRepository.findById(savedImage.getId());
        Assertions.assertTrue(byId.isPresent());
        Assertions.assertEquals("NewFileName", byId.get().getFileName());
    }
}
