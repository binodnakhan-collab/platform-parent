package com.platform.shared.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SliceResponse<T> {

    private List<T> content;
    private boolean hasNext;

}