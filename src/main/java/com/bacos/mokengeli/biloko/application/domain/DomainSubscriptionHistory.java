package com.bacos.mokengeli.biloko.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Historique des changements de plan pour un tenant.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainSubscriptionHistory {
    private Long id;
    private Long tenantId;
    private DomainSubscriptionPlan subscriptionPlan;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;      // null si en cours
    private OffsetDateTime createdAt;
}
