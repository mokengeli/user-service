package com.bacos.mokengeli.biloko.infrastructure.mapper;

import com.bacos.mokengeli.biloko.application.domain.AddonRequestStatusEnum;
import com.bacos.mokengeli.biloko.application.domain.DomainAddonRequest;
import com.bacos.mokengeli.biloko.infrastructure.model.AddonRequest;
import com.bacos.mokengeli.biloko.infrastructure.model.User;
import lombok.experimental.UtilityClass;

/**
 * Mapper pour la conversion entre AddonRequest (Entity JPA) et DomainAddonRequest (Domain).
 */
@UtilityClass
public class AddonRequestMapper {

    /**
     * Convertit une entité JPA AddonRequest en objet métier DomainAddonRequest.
     *
     * @param entity Entité JPA
     * @return Objet métier
     */
    public DomainAddonRequest toDomain(AddonRequest entity) {
        if (entity == null) return null;

        return DomainAddonRequest.builder()
                .id(entity.getId())
                .ticketId(entity.getTicketId())
                .tenantId(entity.getTenant() != null ? entity.getTenant().getId() : null)
                .tenantCode(entity.getTenant() != null ? entity.getTenant().getCode() : null)
                .tenantName(entity.getTenant() != null ? entity.getTenant().getName() : null)
                .featureCode(entity.getFeature() != null ? entity.getFeature().getCode() : null)
                .requestedByEmployeeNumber(entity.getRequestedBy() != null ? entity.getRequestedBy().getEmployeeNumber() : null)
                .requestedByName(entity.getRequestedBy() != null ? getUserFullName(entity.getRequestedBy()) : null)
                .message(entity.getMessage())
                .status(AddonRequestStatusEnum.getByCode(entity.getStatus()))
                .createdAt(entity.getCreatedAt())
                .processedAt(entity.getProcessedAt())
                .processedByEmployeeNumber(entity.getProcessedBy() != null ? entity.getProcessedBy().getEmployeeNumber() : null)
                .processedByName(entity.getProcessedBy() != null ? getUserFullName(entity.getProcessedBy()) : null)
                .build();
    }

    /**
     * Convertit un objet métier DomainAddonRequest en entité JPA AddonRequest.
     * Note: tenant, feature et users doivent être settés séparément dans l'Adapter.
     *
     * @param domain Objet métier
     * @return Entité JPA (partielle)
     */
    public AddonRequest toEntity(DomainAddonRequest domain) {
        if (domain == null) return null;

        return AddonRequest.builder()
                .id(domain.getId())
                .ticketId(domain.getTicketId())
                .message(domain.getMessage())
                .status(domain.getStatus() != null ? domain.getStatus().getCode() : null)
                .createdAt(domain.getCreatedAt())
                .processedAt(domain.getProcessedAt())
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
