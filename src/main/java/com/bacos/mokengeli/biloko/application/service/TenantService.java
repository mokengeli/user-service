package com.bacos.mokengeli.biloko.application.service;


import com.bacos.mokengeli.biloko.application.domain.DomainTenant;
import com.bacos.mokengeli.biloko.application.domain.model.ConnectedUser;
import com.bacos.mokengeli.biloko.application.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class TenantService {
    private final UserAppService userAppService;
    private final TenantPort tenantPort;

    @Autowired
    public TenantService(UserAppService userAppService, TenantPort tenantPort) {
        this.userAppService = userAppService;
        this.tenantPort = tenantPort;
    }

    public List<DomainTenant> getAllTenantsByOrganisation() throws ServiceException {
        ConnectedUser connectedUser = this.userAppService.getConnectedUser();
        String employeeNumber = connectedUser.getEmployeeNumber();
        if (!this.userAppService.isAdminUser()) {
            String errorId = UUID.randomUUID().toString();
            log.error("[{}]: User [{}] try to get All tenants but don't have permission", errorId, employeeNumber);
            throw new ServiceException(errorId, "You don't have permission to perform getAll tenants");
        }
        Optional<List<DomainTenant>> domainProducts = this.tenantPort.getAllTenants();
        return domainProducts.orElse(Collections.emptyList());
    }

    public DomainTenant getTenantByCode(String tenantCode) throws ServiceException {
        ConnectedUser connectedUser = this.userAppService.getConnectedUser();
        String employeeNumber = connectedUser.getEmployeeNumber();
        if (this.userAppService.isAdminUser()
                || tenantCode.equals(connectedUser.getTenantCode())) {
            Optional<DomainTenant> domainTenant = this.tenantPort.getTenantByCode(tenantCode);
            if (domainTenant.isPresent()) {
                return domainTenant.get();
            }
            String errorId = UUID.randomUUID().toString();
            log.error("[{}]: User [{}]No tenant code found with code [{}]", errorId, employeeNumber, tenantCode);
            throw new ServiceException(errorId, "No tenant code found with code [" + tenantCode + "]");
        }
        String errorId = UUID.randomUUID().toString();
        log.error("[{}]: User [{}] try to get tenant [{}] but don't have permission", errorId, employeeNumber, tenantCode);
        throw new ServiceException(errorId, "You don't have permission to perform get tenant");
    }
}
