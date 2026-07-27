package com.platform.shared.payload.request;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRequest extends BaseRequest {

    @Builder.Default
    private List<FilterRequest> filters = new ArrayList<>();
    private PageRequest page;

}