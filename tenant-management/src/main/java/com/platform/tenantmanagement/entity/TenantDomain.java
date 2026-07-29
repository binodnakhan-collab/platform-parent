package com.platform.tenantmanagement.entity;

import com.platform.shared.entity.AuditableBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tenant_domain", schema = "tenant_management")
public class TenantDomain extends AuditableBaseEntity {

    @Column(name = "domain", length = 100)
    private String domain;

    @Column(name = "is_primary")
    private boolean primary;

    @Column(name = "is_custom")
    private boolean custom;

    @Column(name = "ssl_status", nullable = false, length = 50)
    private String sslStatus;

    @Column(name = "verified_at", nullable = false)
    private LocalDateTime verifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
}
