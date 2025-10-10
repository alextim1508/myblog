package com.alextim.myblog.service;

import com.alextim.myblog.model.Tag;
import com.alextim.myblog.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository repository;

    @Override
    public Tag save(Tag tag, Long postId) {
        if(tag.getId() == null)
            tag = repository.save(tag);

        try {
            repository.saveRelationship(tag.getId(), postId);
        } catch (DuplicateKeyException e) {
            log.warn("relationship post_tag already exists. post_id: {} tag_id: {}", postId, tag.getId());
        }
        return tag;
    }

    @Override
    public Set<Tag> save(String content) {
        Set<String> titles = Arrays.stream(content.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        Set<Tag> tags = repository.findTagsByTitleIn(titles);

        titles.stream()
                .map(Tag::new)
                .filter(tag -> !tags.contains(tag))
                .forEach(tag -> {
                    tags.add(repository.save(tag));
                });

        return tags;
    }

    @Override
    public List<Tag> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Tag> findTagByTitle(String title) {
        return repository.findTagByTitle(title);
    }

    @Override
    public Set<Tag> findTagsByTitles(Set<String> titles) {
        return repository.findTagsByTitleIn(titles);
    }

    @Override
    public void deleteById(long id) {
        repository.deleteRelationshipByTagId(id);
        repository.deleteById(id);
    }

    @Override
    public void deleteRelationshipByTagId(long id) {
        repository.deleteRelationshipByTagId(id);
    }

    public static String tagsToString(Set<Tag> tags) {
        return tags != null ? tags.stream().map(Tag::getTitle).collect(Collectors.joining(", ")) : null;
    }
}
