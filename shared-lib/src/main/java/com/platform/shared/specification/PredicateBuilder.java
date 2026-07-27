package com.platform.shared.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public final class PredicateBuilder {

    private PredicateBuilder() {
    }

    public static <T> Predicate build(Root<T> root, CriteriaBuilder cb, SearchFilter filter) {
        Path<?> path = root.get(filter.getField());
        return switch (filter.getOperator()) {

            case EQUAL ->
                    cb.equal(path, filter.getValue());

            case NOT_EQUAL ->
                    cb.notEqual(path, filter.getValue());

            case LIKE ->
                    cb.like(
                            cb.lower(path.as(String.class)),
                            "%" + filter.getValue().toString().toLowerCase() + "%");

            case STARTS_WITH ->
                    cb.like(
                            cb.lower(path.as(String.class)),
                            filter.getValue().toString().toLowerCase() + "%");

            case ENDS_WITH ->
                    cb.like(
                            cb.lower(path.as(String.class)),
                            "%" + filter.getValue().toString().toLowerCase());

            case GREATER_THAN ->
                    cb.greaterThan(
                            path.as(String.class),
                            filter.getValue().toString());

            case GREATER_THAN_EQUAL ->
                    cb.greaterThanOrEqualTo(
                            path.as(String.class),
                            filter.getValue().toString());

            case LESS_THAN ->
                    cb.lessThan(
                            path.as(String.class),
                            filter.getValue().toString());

            case LESS_THAN_EQUAL ->
                    cb.lessThanOrEqualTo(
                            path.as(String.class),
                            filter.getValue().toString());

            case IS_NULL ->
                    cb.isNull(path);

            case IS_NOT_NULL ->
                    cb.isNotNull(path);

            default ->
                    throw new UnsupportedOperationException(
                            "Operator not implemented: " + filter.getOperator());
        };
    }
}