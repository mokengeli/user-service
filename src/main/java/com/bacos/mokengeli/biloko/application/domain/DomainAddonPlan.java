package com.bacos.mokengeli.biloko.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Représente un plan tarifaire d'addon (ex: CRM Basic, CRM Pro, etc.).
 * Objet métier pur pour affichage des offres disponibles.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainAddonPlan {
    private Long id;                        // ID technique (BIGSERIAL)
    private String code;                    // Code métier unique (ex: "CRM_STARTER", "CRM_PRO")
    private String name;                    // Nom commercial
    private String description;             // Description du plan
    private BigDecimal price;               // Prix mensuel/annuel
    private String currency;                // USD, EUR, etc.
    private String billingPeriod;           // monthly, yearly
    private String category;                // crm, reservations, onlineOrdering, all
    private BigDecimal savings;             // Économie vs achat séparé (pour bundles)
    private Boolean isActive;               // Plan actif ou archivé
    private List<String> includedFeatures;  // Liste des IDs de features incluses
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
