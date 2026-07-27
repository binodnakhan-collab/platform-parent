package com.platform.shared.mapper;

import java.util.Collections;
import java.util.List;

public final class MapperUtil {

    private MapperUtil() {
    }

    public static <T> List<T> emptyIfNull(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    public static String trim(String value) {
        return value == null ? null : value.trim();
    }

}