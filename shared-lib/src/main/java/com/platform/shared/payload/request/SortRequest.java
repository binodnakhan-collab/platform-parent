package com.platform.shared.payload.request;

import com.platform.shared.enums.SortDirection;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SortRequest {

    private String property;

    @Builder.Default
    private SortDirection direction = SortDirection.ASC;

}