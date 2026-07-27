package com.platform.shared.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface JpaSpecificationExecutorEx<T> extends JpaSpecificationExecutor<T> {

    default Optional<T> findOneOrEmpty(Specification<T> specification) {
        return findOne(specification);
    }

}