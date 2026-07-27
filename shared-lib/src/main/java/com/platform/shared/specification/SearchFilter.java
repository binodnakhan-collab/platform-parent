package com.platform.shared.specification;

import com.platform.shared.enums.SearchOperator;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchFilter {

    /**
     * Entity field.
     */
    private String field;
    private Object value;

    /**
     * Search operator.
     */
    @Builder.Default
    private SearchOperator operator = SearchOperator.EQUAL;

}