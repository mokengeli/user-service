package com.bacos.mokengeli.biloko.infrastructure.mapper;


import com.bacos.mokengeli.biloko.application.domain.DomainTenant;
import com.bacos.mokengeli.biloko.infrastructure.model.Tenant;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TenantMapper {

    public Tenant toEntity(DomainTenant domainTenantContext) {
        if (domainTenantContext == null) {
            return null;
        }

        return Tenant.builder()
                .id(domainTenantContext.getId())
                .code(domainTenantContext.getCode())
                .name(domainTenantContext.getName())
                .email(domainTenantContext.getEmail())
                .build();
    }

    public DomainTenant toDomain(Tenant tenantContext) {
        if (tenantContext == null) {
            return null;
        }

        return DomainTenant.builder()
                .id(tenantContext.getId())
                .code(tenantContext.getCode())
                .name(tenantContext.getName())
                .email(tenantContext.getEmail())
                .createdAt(tenantContext.getCreatedAt())
                .updatedAt(tenantContext.getUpdatedAt())
                .build();
    }

    public DomainTenant toLightDomain(Tenant tenantContext) {
        if (tenantContext == null) {
            return null;
        }

        return DomainTenant.builder()
                .id(tenantContext.getId())
                .code(tenantContext.getCode())
                .name(tenantContext.getName())
                .email(tenantContext.getEmail())
                .build();
    }
}
