package com.bacos.mokengeli.biloko.infrastructure.adapter;

import com.bacos.mokengeli.biloko.application.domain.DomainTenant;
import com.bacos.mokengeli.biloko.application.service.TenantPort;
import com.bacos.mokengeli.biloko.infrastructure.mapper.TenantMapper;
import com.bacos.mokengeli.biloko.infrastructure.model.Tenant;
import com.bacos.mokengeli.biloko.infrastructure.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TenantAdapter implements TenantPort {
    private TenantRepository tenantRepository;
    @Autowired
    public TenantAdapter(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }
    @Override
    public Optional<List<DomainTenant>> getAllTenants() {
        List<Tenant> tenants = this.tenantRepository.findAll();
        if (tenants.isEmpty()) {
            return Optional.empty();
        }
        List<DomainTenant> list = tenants.stream().map(TenantMapper::toLightDomain).toList();
        return Optional.of(list);
    }

    @Override
    public Optional<DomainTenant> getTenantByCode(String tenantCode) {
        Tenant tenant = this.tenantRepository.findByCode(tenantCode);
        if (tenant == null) {
            return Optional.empty();
        }
        return Optional.of(TenantMapper.toLightDomain(tenant));
    }
}
