package com.alextim.myblog.repository;

import com.alextim.myblog.model.Tag;

import java.util.Optional;
import java.util.Set;


public interface TagRepository extends AbstractRepository<Tag> {

    Tag save(Tag tag);

    void saveRelationship(Long tagId, Long postId);

    Set<Tag> findTagsByPostId(Long postId);

    Optional<Tag> findTagByTitle(String content);

    Set<Tag> findTagsByTitleIn(Set<String> titles);

    void deleteRelationshipByTagId(Long id);

    void deleteRelationships();
}