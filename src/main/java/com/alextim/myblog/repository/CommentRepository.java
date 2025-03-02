package com.alextim.myblog.repository;

import com.alextim.myblog.model.Comment;
import com.alextim.myblog.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    int countByPost(Post post);
}
