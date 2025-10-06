package com.bacos.mokengeli.biloko.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Représente une feature (fonctionnalité) disponible dans le système.
 * Objet métier pur pour le catalogue des features.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainFeature {
    private Long id;                    // ID technique (BIGSERIAL)
    private String code;                // Code métier unique (ex: "basicCRM", "emailMarketing")
    private String name;                // Nom affichable (ex: "CRM Basique")
    private String description;         // Description de la feature
    private String category;            // Catégorie (crm, reservations, onlineOrdering)
    private Boolean isActive;           // Feature active ou archivée
    private Integer displayOrder;       // Ordre d'affichage
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
