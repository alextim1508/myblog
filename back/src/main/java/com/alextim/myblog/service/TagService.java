package com.alextim.myblog.service;

import com.alextim.myblog.model.Tag;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagService {

    Tag save(Tag tag, Long postId);

    Set<Tag> save(String content);

    Optional<Tag> findTagByTitle(String title);

    Set<Tag> findTagsByTitles(Set<String> titles);

    List<Tag> findAll();

    void deleteById(long id);

    void deleteRelationshipByTagId(long id);
}
