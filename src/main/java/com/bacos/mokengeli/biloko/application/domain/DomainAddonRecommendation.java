package com.bacos.mokengeli.biloko.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Représente une recommandation personnalisée de plan pour un tenant.
 * Utilisé dans la réponse GET /api/tenant/{code}/addons/plans
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainAddonRecommendation {
    private String tenantCode;              // Code du tenant à qui est faite la recommandation
    private String planCode;                // Code métier du plan recommandé (ex: "CRM_STARTER")
    private String reason;                  // Raison de la recommandation
    private Integer discount;               // Pourcentage de réduction (optionnel)
    private OffsetDateTime validUntil;      // Date limite de l'offre (optionnel)
}
