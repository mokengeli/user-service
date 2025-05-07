package com.bacos.mokengeli.biloko.infrastructure.mapper;

import com.bacos.mokengeli.biloko.application.domain.DomainTenant;
import com.bacos.mokengeli.biloko.infrastructure.model.Tenant;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TenantMapper {

    public Tenant toEntity(DomainTenant domain) {
        if (domain == null) return null;
        return Tenant.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .name(domain.getName())
                .email(domain.getEmail())
                .establishmentType(
                        EstablishmentTypeMapper.toEntity(domain.getEstablishmentType())
                )
                .subscriptionPlan(
                        SubscriptionPlanMapper.toEntity(domain.getSubscriptionPlan())
                )
                .build();
    }

    public DomainTenant toDomain(Tenant entity) {
        if (entity == null) return null;
        return DomainTenant.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .email(entity.getEmail())
                .establishmentType(
                        EstablishmentTypeMapper.toDomain(entity.getEstablishmentType())
                )
                .subscriptionPlan(
                        SubscriptionPlanMapper.toDomain(entity.getSubscriptionPlan())
                )
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public DomainTenant toLightDomain(Tenant entity) {
        if (entity == null) return null;
        // on conserve ici uniquement l’ID/code/nom/email, sans type ni plan
        return DomainTenant.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .establishmentType(
                        EstablishmentTypeMapper.toDomain(entity.getEstablishmentType())
                )
                .email(entity.getEmail())
                .build();
    }
}
