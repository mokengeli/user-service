package com.bacos.mokengeli.biloko.application.port;

import com.bacos.mokengeli.biloko.application.domain.DomainTenant;
import com.bacos.mokengeli.biloko.application.exception.ServiceException;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface TenantPort {
    Optional<List<DomainTenant>> getAllTenants();

    Optional<DomainTenant> getTenantByCode(String tenantCode);

    Page<DomainTenant> findAllTenantsByTenant(int page, int size, String search);

    DomainTenant createNewTenant(DomainTenant domainTenant) throws ServiceException;
}
