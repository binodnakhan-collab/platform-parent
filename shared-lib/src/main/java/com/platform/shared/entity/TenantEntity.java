package com.platform.shared.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class TenantEntity extends AuditableBaseEntity {

    @Column(nullable = false, length = 50)
    private String tenantId;

}