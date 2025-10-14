package com.alextim.myblog.repository;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RepositoryTest {

    @Autowired
    PostRepository postRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    CommentRepository commentRepository;

    @AfterEach
    void tearDown() {
        commentRepository.delete();
        imageRepository.delete();
        tagRepository.deleteRelationships();
        tagRepository.delete();
        postRepository.delete();
    }
}
