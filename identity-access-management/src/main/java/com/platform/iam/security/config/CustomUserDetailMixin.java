package com.platform.iam.security.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public abstract class CustomUserDetailMixin {

    // Leave empty. This adds annotations to your class dynamically.
}
