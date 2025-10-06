package com.bacos.mokengeli.biloko.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Représente une demande d'activation d'addon faite par un manager.
 * Workflow: Manager demande → Admin approuve/rejette
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainAddonRequest {
    private Long id;
    private String ticketId;                    // Format: ADDON-2025-001
    private Long tenantId;
    private String tenantCode;
    private String tenantName;
    private String featureCode;                 // Code métier de la feature (ex: "basicCRM")
    private String requestedByEmployeeNumber;
    private String requestedByName;             // Nom complet du manager qui a demandé
    private String message;                     // Message optionnel du manager
    private AddonRequestStatusEnum status;      // PENDING, APPROVED, REJECTED
    private OffsetDateTime createdAt;
    private OffsetDateTime processedAt;
    private String processedByEmployeeNumber;   // Admin qui a traité la demande
    private String processedByName;             // Nom complet de l'admin
}
