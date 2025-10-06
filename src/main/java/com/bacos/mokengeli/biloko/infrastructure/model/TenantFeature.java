package com.bacos.mokengeli.biloko.infrastructure.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Entité JPA représentant une feature activée (ou non) pour un tenant.
 * Correspond à la table tenant_features.
 */
@Entity
@Table(name = "tenant_features")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_id", nullable = false)
    private Feature feature;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(name = "activated_at")
    private OffsetDateTime activatedAt;

    @Column(name = "deactivated_at")
    private OffsetDateTime deactivatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activated_by_id")
    private User activatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deactivated_by_id")
    private User deactivatedBy;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
