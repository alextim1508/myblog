package com.alextim.myblog.repository;

import com.alextim.myblog.model.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {

    List<Tag> findAll();

    Optional<Tag> findTagByTitle(String content);

    Set<Tag> findTagsByTitleIn(Set<String> titles);
}