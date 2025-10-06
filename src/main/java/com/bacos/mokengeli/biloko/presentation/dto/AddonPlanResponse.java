package com.bacos.mokengeli.biloko.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO pour la réponse de l'endpoint GET /api/user/tenant-features/plans
 * Représente un plan tarifaire d'addon avec ses features incluses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddonPlanResponse {

    /**
     * Code métier unique du plan (ex: "CRM_STARTER", "CRM_PRO").
     */
    private String code;

    /**
     * Nom commercial du plan (ex: "CRM Starter").
     */
    private String name;

    /**
     * Description détaillée du plan.
     */
    private String description;

    /**
     * Prix du plan.
     */
    private BigDecimal price;

    /**
     * Devise (ex: "USD", "EUR").
     */
    private String currency;

    /**
     * Période de facturation (ex: "monthly", "yearly").
     */
    private String billingPeriod;

    /**
     * Catégorie du plan (ex: "crm", "reservations", "onlineOrdering", "all").
     */
    private String category;

    /**
     * Économie par rapport à l'achat séparé (pour les bundles).
     * Null si pas de réduction.
     */
    private BigDecimal savings;

    /**
     * Indique si le plan est actif (disponible à la vente).
     */
    private Boolean isActive;

    /**
     * Liste des features incluses dans ce plan.
     * Chaque élément contient featureId et featureName.
     */
    private List<FeatureInfo> includedFeatures;

    /**
     * Date de création du plan.
     */
    private OffsetDateTime createdAt;

    /**
     * Date de dernière mise à jour.
     */
    private OffsetDateTime updatedAt;

    /**
     * Informations d'une feature incluse dans un plan.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureInfo {
        /**
         * Code métier de la feature (ex: "basicCRM", "emailMarketing").
         */
        private String featureCode;

        /**
         * Nom lisible de la feature (ex: "CRM Basique").
         */
        private String featureName;
    }
}
