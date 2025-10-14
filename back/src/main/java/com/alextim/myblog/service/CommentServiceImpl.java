package com.alextim.myblog.service;

import com.alextim.myblog.exception.ClientException;
import com.alextim.myblog.model.Comment;
import com.alextim.myblog.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;

    @Override
    public Comment save(Comment comment) {
        log.debug("Saving comment with ID: {}", comment.getId());

        if (comment.getId() == null) {
            log.info("Creating new comment for post ID: {}", comment.getPostId());
            Comment savedComment = repository.save(comment);
            log.info("Successfully created comment with ID: {}", savedComment.getId());
            return savedComment;
        } else {
            log.info("Updating existing comment with ID: {}", comment.getId());
            int updatedRowCount = repository.update(comment);

            if (updatedRowCount != 1) {
                log.error("Failed to update comment with ID: {}. Updated row count: {}", comment.getId(), updatedRowCount);
                throw new ClientException("Update comment exception. updatedRowCount: " + updatedRowCount);
            }

            log.info("Successfully updated comment with ID: {}", comment.getId());

            return repository.findById(comment.getId())
                    .orElseThrow(() -> new ClientException("Comment with ID " + comment.getId() + " not found after update"));
        }
    }

    @Override
    public Comment findById(long id) {
        log.info("Finding comment by ID: {}", id);
        Comment comment = repository.findById(id).orElseThrow(() ->
                new ClientException("Comment with ID " + id + " does not exist"));
        log.debug("Found comment: {}", comment.getText());
        return comment;
    }

    @Override
    public int countByPostId(long postId) {
        log.info("Counting comments for post ID: {}", postId);
        int count = repository.countByPostId(postId);
        log.debug("Comment count for post ID {}: {}", postId, count);
        return count;
    }

    @Override
    public List<Comment> findAll(int page, int size) {
        log.info("Finding all comments. Page: {}, Size: {}", page, size);
        List<Comment> comments = repository.findAll(size, page * size);
        log.debug("Found {} comments", comments.size());
        return comments;
    }

    @Override
    public List<Comment> findByPostId(long postId) {
        log.info("Finding comments by post ID : {}", postId);
        List<Comment> comments = repository.findByPostId(postId);
        log.debug("Found {} comments", comments.size());
        return comments;
    }

    @Override
    public void deleteByPostId(long postId) {
        log.info("Deleting all comments for post ID: {}", postId);
        repository.deleteByPostId(postId);
        log.info("Successfully deleted comments for post ID: {}", postId);
    }

    @Override
    public void deleteById(long id) {
        log.info("Deleting comment with ID: {}", id);
        repository.deleteById(id);
        log.info("Successfully deleted comment with ID: {}", id);
    }
}