package com.platform.shared.payload.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkRequest<T> {

    private List<T> items;

}