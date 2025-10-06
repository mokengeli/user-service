package com.bacos.mokengeli.biloko.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO pour la réponse de l'endpoint GET /api/user/tenant-features/status
 * Retourne les features groupées par catégorie avec leur statut.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddonStatusResponse {

    /**
     * Liste des codes de features activées (pour accès rapide).
     * Exemple: ["basicCRM", "loyaltyProgram"]
     */
    private List<String> activatedFeatures;

    /**
     * Features groupées par catégorie (crm, reservations, onlineOrdering).
     * Structure: { "crm": { features: {...} }, "reservations": {...}, ... }
     */
    private Map<String, GroupFeatures> groups;

    /**
     * Représente un groupe de features d'une catégorie.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupFeatures {
        /**
         * Map des features du groupe.
         * Clé = featureCode (ex: "basicCRM")
         * Valeur = Détails de la feature
         */
        private Map<String, FeatureDetails> features;
    }

    /**
     * Détails d'une feature spécifique.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureDetails {
        /**
         * Code métier de la feature (ex: "basicCRM", "emailMarketing").
         */
        private String featureCode;

        /**
         * Nom lisible de la feature (ex: "CRM Basique").
         */
        private String featureName;

        /**
         * État de la feature (true = activée, false = désactivée).
         */
        private Boolean enabled;

        /**
         * Date d'activation (null si jamais activée).
         */
        private OffsetDateTime activatedAt;
    }
}
