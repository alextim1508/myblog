package com.alextim.myblog.service;

import com.alextim.myblog.model.Tag;
import com.alextim.myblog.repository.TagRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TagServiceTest {

    @MockitoBean
    private TagRepository repository;

    @Autowired
    private TagServiceImpl service;

    @Test
    public void save_shouldSaveTagsAsString_whenTwoTagsAreExistAndOneTagIsNew() {
        when(repository.findTagsByTitleIn(anyList()))
                .thenReturn(Arrays.asList(new Tag("tag1"), new Tag("tag2")));

        when(repository.save(any(Tag.class))).thenAnswer(invocation -> {
            Tag tag = invocation.getArgument(0);
            tag.setId(1L);
            return tag;
        });

        List<String> content = List.of("tag1", "tag2", "tag3");

        List<Tag> tags = service.save(content);

        verify(repository).findTagsByTitleIn(List.of("tag1", "tag2", "tag3"));
        Assertions.assertEquals(3, tags.size());
    }

    @Test
    public void save_shouldCallSave() {
        when(repository.save(any(Tag.class))).thenReturn(new Tag(1L, "tag"));

        Tag savedTag = service.save(new Tag("tag"), 1L);

        verify(repository).save(new Tag("tag"));
        Assertions.assertEquals(new Tag("tag"), savedTag);
    }

    @Test
    public void findAll_shouldCallFindAll() {
        service.findAll();

        verify(repository).findAll();
    }

    @Test
    public void findTagByTitle_shouldCallFindTagByTitle() {
        when(repository.findTagByTitle(any())).thenReturn(Optional.of(new Tag("tag")));

        Optional<Tag> tagByTitle = service.findTagByTitle("tag");

        verify(repository).findTagByTitle("tag");
        Assertions.assertEquals(Optional.of(new Tag("tag")), tagByTitle);
    }

    @Test
    public void findTagsByTitles_shouldCallFindTagsByTitleIn() {
        when(repository.findTagsByTitleIn(anyList())).thenReturn(List.of(new Tag("tag1"), new Tag("tag2")));

        List<Tag> tagsByTitles = service.findTagsByTitles(List.of("tag1", "tag2"));

        verify(repository).findTagsByTitleIn(List.of("tag1", "tag2"));
        Assertions.assertEquals(List.of(new Tag("tag1"), new Tag("tag2")), tagsByTitles);
    }

    @Test
    public void delete_shouldCallDelete() {
        service.deleteById(1L);

        verify(repository).deleteById(1L);
    }
}
