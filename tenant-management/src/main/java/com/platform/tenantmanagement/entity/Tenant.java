package com.platform.tenantmanagement.entity;

import com.platform.shared.entity.AuditableBaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tenant", schema = "tenant_management")
public class Tenant extends AuditableBaseEntity {

    private String name;
    private String slug;
    private String status;
    private String isolationMode;
    private String dbConfigRef;
    private String region;
    private String timezone;
    private String locale;
    private LocalDateTime trialEndAt;
    private String metadata;
}
