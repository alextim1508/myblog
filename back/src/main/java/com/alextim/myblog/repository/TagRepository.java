package com.alextim.myblog.repository;

import com.alextim.myblog.model.Tag;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface TagRepository extends AbstractRepository<Tag> {

    Tag save(Tag tag);

    void saveRelationship(long tagId, long postId);

    List<Tag> findTagsByPostId(long postId);

    Optional<Tag> findTagByTitle(String content);

    List<Tag> findTagsByTitleIn(List<String> titles);

    void deleteRelationshipByPostId(long id);

    void deleteRelationships();
}