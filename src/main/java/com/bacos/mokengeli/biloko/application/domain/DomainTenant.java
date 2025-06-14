package com.bacos.mokengeli.biloko.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;


/**
 * Représente un locataire (enseigne) avec son type et son plan.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainTenant {
    private Long id;
    private String code;
    private String name;
    private String email;
    private DomainEstablishmentType establishmentType;
    private DomainSubscriptionPlan subscriptionPlan;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
