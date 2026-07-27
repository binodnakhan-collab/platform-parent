package com.platform.shared.mapper;

import org.mapstruct.MappingTarget;

import java.util.List;

public interface BaseMapper<E, RQ, RS> {

    E toEntity(RQ request);

    RS toResponse(E entity);

    List<RS> toResponse(List<E> entities);

    List<E> toEntity(List<RQ> requests);

    void updateEntity(RQ request, @MappingTarget E entity);

}