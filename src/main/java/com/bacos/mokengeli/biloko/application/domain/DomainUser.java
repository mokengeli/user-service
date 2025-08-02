package com.bacos.mokengeli.biloko.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Utilisateur, avec infos sur son tenant (code, type, plan…).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainUser {
    private Long id;
    private Long tenantId;
    private String tenantName;
    private String tenantCode;
    private DomainEstablishmentType tenantEstablishmentType;
    private DomainSubscriptionPlan tenantSubscriptionPlan;
    private String firstName;
    private String lastName;
    private String postName;
    private String userName;
    private String employeeNumber;
    private String email;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<String> roles;
    private List<String> permissions;
    private boolean hasValidationPin;
}
