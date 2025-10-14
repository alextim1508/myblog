package com.alextim.myblog.service;

import com.alextim.myblog.model.Tag;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagService {

    Tag save(Tag tag, long postId);

    List<Tag> save(List<String> tagTitles);

    Optional<Tag> findTagByTitle(String title);

    List<Tag> findAll();

    List<Tag> findTagsByTitles(List<String> titles);

    List<Tag> findTagsByPostId(long postId);

    void deleteById(long id);

    void deleteRelationshipByPostId(long id);
}
