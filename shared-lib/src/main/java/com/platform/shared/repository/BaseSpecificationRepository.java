package com.platform.shared.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface BaseSpecificationRepository<T, ID> extends BaseRepository<T, ID> {

    default Page<T> search(Specification<T> specification, Pageable pageable) {
        return findAll(specification, pageable);
    }

    default List<T> search(Specification<T> specification) {
        return findAll(specification);
    }
}