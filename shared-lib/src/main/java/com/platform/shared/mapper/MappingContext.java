package com.platform.shared.mapper;

import java.util.IdentityHashMap;

public class MappingContext {

    private final IdentityHashMap<Object, Object> knownInstances =
            new IdentityHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T getMappedInstance(Object source) {
        return (T) knownInstances.get(source);
    }

    public void storeMappedInstance(Object source, Object target) {
        knownInstances.put(source, target);
    }

}