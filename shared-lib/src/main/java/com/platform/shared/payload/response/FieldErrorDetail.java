package com.platform.shared.payload.response;

import java.util.List;

public record FieldErrorDetail(String field, List<String> messages) {

}
