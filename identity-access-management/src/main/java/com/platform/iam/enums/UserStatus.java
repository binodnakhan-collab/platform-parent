package com.platform.iam.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {

    ACTIVE(
            "ACTIVE",
            "Active",
            "User account is active and can access the system."
    ),

    INACTIVE(
            "INACTIVE",
            "Inactive",
            "User account is inactive and cannot access the system."
    ),

    PENDING(
            "PENDING",
            "Pending",
            "User account is awaiting verification or approval."
    ),

    LOCKED(
            "LOCKED",
            "Locked",
            "User account has been locked due to security reasons."
    ),

    SUSPENDED(
            "SUSPENDED",
            "Suspended",
            "User account has been temporarily suspended."
    ),

    DISABLED(
            "DISABLED",
            "Disabled",
            "User account has been permanently disabled by an administrator."
    ),

    DELETED(
            "DELETED",
            "Deleted",
            "User account has been soft deleted."
    );

    private final String key;
    private final String value;
    private final String description;
}