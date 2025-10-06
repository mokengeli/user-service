package com.bacos.mokengeli.biloko.infrastructure.mapper;

import com.bacos.mokengeli.biloko.application.domain.DomainAddonPlan;
import com.bacos.mokengeli.biloko.infrastructure.model.AddonPlan;
import com.bacos.mokengeli.biloko.infrastructure.model.PlanFeature;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper pour la conversion entre AddonPlan (Entity JPA) et DomainAddonPlan (Domain).
 */
@UtilityClass
public class AddonPlanMapper {

    /**
     * Convertit une entité JPA AddonPlan en objet métier DomainAddonPlan.
     *
     * @param entity Entité JPA
     * @return Objet métier
     */
    public DomainAddonPlan toDomain(AddonPlan entity) {
        if (entity == null) return null;

        // Extraction des feature codes depuis la relation PlanFeature
        List<String> featureCodes = entity.getFeatures() != null
                ? entity.getFeatures().stream()
                        .map(pf -> pf.getFeature().getCode())
                        .collect(Collectors.toList())
                : Collections.emptyList();

        return DomainAddonPlan.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .price(entity.getPrice())
                .currency(entity.getCurrency())
                .billingPeriod(entity.getBillingPeriod())
                .isActive(entity.getIsActive())
                .includedFeatures(featureCodes)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convertit un objet métier DomainAddonPlan en entité JPA AddonPlan.
     * Note: la relation PlanFeature doit être gérée séparément dans l'Adapter.
     *
     * @param domain Objet métier
     * @return Entité JPA (sans les features)
     */
    public AddonPlan toEntity(DomainAddonPlan domain) {
        if (domain == null) return null;

        return AddonPlan.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .name(domain.getName())
                .description(domain.getDescription())
                .category(domain.getCategory())
                .price(domain.getPrice())
                .currency(domain.getCurrency())
                .billingPeriod(domain.getBillingPeriod())
                .isActive(domain.getIsActive())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
