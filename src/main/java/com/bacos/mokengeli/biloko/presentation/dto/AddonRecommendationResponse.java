package com.bacos.mokengeli.biloko.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO pour la réponse de l'endpoint GET /api/user/tenant-features/recommendations
 * Retourne les recommandations de plans pour un tenant.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddonRecommendationResponse {

    /**
     * Code du tenant concerné par ces recommandations.
     */
    private String tenantCode;

    /**
     * Liste des plans recommandés pour ce tenant.
     */
    private List<RecommendationItem> recommendedPlans;

    /**
     * Pourcentage de réduction global offert (optionnel).
     * Exemple: 10.0 pour 10% de réduction.
     */
    private Double discount;

    /**
     * Date limite de validité de l'offre (optionnel).
     * Null si pas de limite de temps.
     */
    private OffsetDateTime validUntil;

    /**
     * Représente un plan recommandé avec sa justification.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationItem {
        /**
         * Code métier du plan recommandé (ex: "CRM_STARTER", "CRM_PRO").
         */
        private String planCode;

        /**
         * Nom du plan (ex: "CRM Pro").
         */
        private String planName;

        /**
         * Raison de la recommandation.
         * Exemple: "Vous avez déjà CRM Basic, passez à Pro pour débloquer email & SMS"
         */
        private String reason;
    }
}
