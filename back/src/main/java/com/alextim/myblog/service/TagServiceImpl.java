package com.alextim.myblog.service;

import com.alextim.myblog.model.Tag;
import com.alextim.myblog.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository repository;

    @Override
    public Tag save(Tag tag, long postId) {
        log.info("Saving tag for postId: {}", postId);
        log.debug("Tag data: {}", tag);

        if (tag.getId() == null) {
            log.info("Tag ID is null, creating new tag with title: {}", tag.getTitle());
            tag = repository.save(tag);
            log.info("Created tag with ID: {}", tag.getId());
        } else {
            log.info("Tag ID is not null, using existing tag with ID: {}", tag.getId());
        }

        try {
            log.info("Saving relationship between tag ID: {} and post ID: {}", tag.getId(), postId);
            repository.saveRelationship(tag.getId(), postId);
            log.info("Relationship saved successfully.");
        } catch (DuplicateKeyException e) {
            log.warn("Relationship post_tag already exists. post_id: {} tag_id: {}", postId, tag.getId());
        }

        return tag;
    }

    @Override
    public List<Tag> save(List<String> tagTitles) {
        log.info("Saving tags by titles: {}", tagTitles);

        List<Tag> tags = new ArrayList<>(repository.findTagsByTitleIn(tagTitles));
        log.debug("Found existing tags: {}", tags);

        List<String> existingTitles = tags.stream()
                .map(Tag::getTitle)
                .toList();

        List<String> newTitles = tagTitles.stream()
                .filter(title -> !existingTitles.contains(title))
                .toList();

        log.info("New titles to create: {}", newTitles);

        newTitles.stream()
                .map(Tag::new)
                .forEach(tag -> {
                    log.info("Creating new tag: {}", tag.getTitle());
                    tags.add(repository.save(tag));
                });

        log.info("Successfully saved/created {} tags", tags.size());
        return tags;
    }

    @Override
    public List<Tag> findAll() {
        log.info("Finding all tags");
        List<Tag> tags = repository.findAll();
        log.info("Found {} tags", tags.size());
        log.debug("Tags: {}", tags);
        return tags;
    }

    @Override
    public Optional<Tag> findTagByTitle(String title) {
        log.info("Finding tag by title: {}", title);
        Optional<Tag> tag = repository.findTagByTitle(title);
        if (tag.isPresent()) {
            log.info("Found tag: {}", tag.get().getTitle());
        } else {
            log.info("Tag with title '{}' not found", title);
        }
        return tag;
    }

    @Override
    public List<Tag> findTagsByTitles(List<String> titles) {
        log.info("Finding tags by titles: {}", titles);
        List<Tag> tags = repository.findTagsByTitleIn(titles);
        log.info("Found {} tags by titles", tags.size());
        log.debug("Tags: {}", tags);
        return tags;
    }

    @Override
    public List<Tag> findTagsByPostId(long postId) {
        log.info("Finding tags for post ID: {}", postId);
        List<Tag> tags = repository.findTagsByPostId(postId);
        log.info("Found {} tags for post ID: {}", tags.size(), postId);
        log.debug("Tags: {}", tags);
        return tags;
    }

    @Override
    public void deleteById(long id) {
        log.info("Deleting tag with ID: {}", id);
        repository.deleteRelationshipByPostId(id);
        log.debug("Deleted relationships for tag ID: {}", id);
        repository.deleteById(id);
        log.info("Successfully deleted tag with ID: {}", id);
    }

    @Override
    public void deleteRelationshipByPostId(long id) {
        log.info("Deleting tag relationships for post ID: {}", id);
        repository.deleteRelationshipByPostId(id);
        log.info("Successfully deleted tag relationships for post ID: {}", id);
    }
}