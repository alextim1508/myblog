package com.alextim.myblog.repository;

import java.util.List;
import java.util.Optional;

public interface AbstractRepository<T> {

    Optional<T> findById(Long id);

    List<T> findAll();

    List<T> findAll(int limit, int offset);

    void delete();

    void deleteById(Long id);

    int count();
}
