package com.bacos.mokengeli.biloko.infrastructure.mapper;

import com.bacos.mokengeli.biloko.application.domain.DomainTenantFeature;
import com.bacos.mokengeli.biloko.infrastructure.model.TenantFeature;
import com.bacos.mokengeli.biloko.infrastructure.model.User;
import lombok.experimental.UtilityClass;

/**
 * Mapper pour la conversion entre TenantFeature (Entity JPA) et DomainTenantFeature (Domain).
 */
@UtilityClass
public class TenantFeatureMapper {

    /**
     * Convertit une entité JPA TenantFeature en objet métier DomainTenantFeature.
     *
     * @param entity Entité JPA
     * @return Objet métier
     */
    public DomainTenantFeature toDomain(TenantFeature entity) {
        if (entity == null) return null;

        return DomainTenantFeature.builder()
                .id(entity.getId())
                .tenantId(entity.getTenant() != null ? entity.getTenant().getId() : null)
                .tenantCode(entity.getTenant() != null ? entity.getTenant().getCode() : null)
                .featureCode(entity.getFeature() != null ? entity.getFeature().getCode() : null)
                .enabled(entity.getEnabled())
                .activatedAt(entity.getActivatedAt())
                .deactivatedAt(entity.getDeactivatedAt())
                .activatedByEmployeeNumber(entity.getActivatedBy() != null ? entity.getActivatedBy().getEmployeeNumber() : null)
                .activatedByName(entity.getActivatedBy() != null ? getUserFullName(entity.getActivatedBy()) : null)
                .deactivatedByEmployeeNumber(entity.getDeactivatedBy() != null ? entity.getDeactivatedBy().getEmployeeNumber() : null)
                .deactivatedByName(entity.getDeactivatedBy() != null ? getUserFullName(entity.getDeactivatedBy()) : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convertit un objet métier DomainTenantFeature en entité JPA TenantFeature.
     * Note: tenant, feature et users doivent être settés séparément dans l'Adapter.
     *
     * @param domain Objet métier
     * @return Entité JPA (partielle)
     */
    public TenantFeature toEntity(DomainTenantFeature domain) {
        if (domain == null) return null;

        return TenantFeature.builder()
                .id(domain.getId())
                .enabled(domain.getEnabled())
                .activatedAt(domain.getActivatedAt())
                .deactivatedAt(domain.getDeactivatedAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    /**
     * Utilitaire pour obtenir le nom complet d'un utilisateur.
     *
     * @param user User entity
     * @return Nom complet (firstName + lastName)
     */
    private String getUserFullName(User user) {
        if (user == null) return null;
        return user.getFirstName() + " " + user.getLastName();
    }
}
