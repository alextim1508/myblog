package com.alextim.myblog.repository;

import com.alextim.myblog.model.Post;
import com.alextim.myblog.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByTags(List<Tag> tags, Pageable pageable);
}

