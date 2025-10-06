package com.bacos.mokengeli.biloko.infrastructure.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Entité JPA représentant la liaison Many-to-Many entre plans et features.
 * Correspond à la table plan_features.
 */
@Entity
@Table(name = "plan_features")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PlanFeature.PlanFeatureId.class)
public class PlanFeature {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private AddonPlan plan;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_id", nullable = false)
    private Feature feature;

    /**
     * Classe interne pour la clé composite (plan_id, feature_id)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanFeatureId implements Serializable {
        private Long plan;        // Correspond à AddonPlan.id (BIGINT)
        private Long feature;     // Correspond à Feature.id (BIGINT)
    }
}
