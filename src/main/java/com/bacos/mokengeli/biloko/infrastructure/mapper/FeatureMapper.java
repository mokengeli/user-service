package com.bacos.mokengeli.biloko.infrastructure.mapper;

import com.bacos.mokengeli.biloko.application.domain.DomainFeature;
import com.bacos.mokengeli.biloko.infrastructure.model.Feature;
import lombok.experimental.UtilityClass;

/**
 * Mapper pour la conversion entre Feature (Entity JPA) et DomainFeature (Domain).
 */
@UtilityClass
public class FeatureMapper {

    /**
     * Convertit une entité JPA Feature en objet métier DomainFeature.
     *
     * @param entity Entité JPA
     * @return Objet métier
     */
    public DomainFeature toDomain(Feature entity) {
        if (entity == null) return null;

        return DomainFeature.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .isActive(entity.getIsActive())
                .displayOrder(entity.getDisplayOrder())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convertit un objet métier DomainFeature en entité JPA Feature.
     *
     * @param domain Objet métier
     * @return Entité JPA
     */
    public Feature toEntity(DomainFeature domain) {
        if (domain == null) return null;

        return Feature.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .name(domain.getName())
                .description(domain.getDescription())
                .category(domain.getCategory())
                .isActive(domain.getIsActive())
                .displayOrder(domain.getDisplayOrder())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
