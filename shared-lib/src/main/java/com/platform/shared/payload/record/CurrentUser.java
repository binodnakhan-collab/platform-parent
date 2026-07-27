package com.platform.shared.payload.record;

import java.util.UUID;

public record CurrentUser(UUID uuid, UUID tenantPublicId, String username) {
}