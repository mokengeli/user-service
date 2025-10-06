package com.bacos.mokengeli.biloko.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Représente une feature activée (ou non) pour un tenant spécifique.
 * Objet métier pur sans dépendances techniques (pas d'annotations JPA).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainTenantFeature {
    private Long id;
    private Long tenantId;
    private String tenantCode;
    private String featureCode;  // Code métier de la feature (ex: "basicCRM", "emailMarketing")
    private Boolean enabled;
    private OffsetDateTime activatedAt;
    private OffsetDateTime deactivatedAt;
    private String activatedByEmployeeNumber;   // Pour audit
    private String activatedByName;             // Nom complet de l'utilisateur qui a activé
    private String deactivatedByEmployeeNumber; // Pour audit
    private String deactivatedByName;           // Nom complet de l'utilisateur qui a désactivé
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
