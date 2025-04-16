package com.bacos.mokengeli.biloko.application.service;

import com.bacos.mokengeli.biloko.application.domain.DomainTenant;

import java.util.List;
import java.util.Optional;

public interface TenantPort {
    Optional<List<DomainTenant>> getAllTenants();

    Optional<DomainTenant> getTenantByCode(String tenantCode);
}
