package com.platform.shared.payload.request;

import lombok.*;
import org.hibernate.query.sqm.ComparisonOperator;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterRequest {

    private String field;
    private ComparisonOperator operator;
    private Object value;

}