package com.bacos.mokengeli.biloko.application.domain.model;

import com.bacos.mokengeli.biloko.application.domain.DomainSubscriptionPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private LocalDate startDate;
    private LocalDate endDate;      // null si en cours
    private LocalDateTime createdAt;
}
