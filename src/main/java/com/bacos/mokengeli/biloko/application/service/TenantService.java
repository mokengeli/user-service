package com.bacos.mokengeli.biloko.application.service;


import com.bacos.mokengeli.biloko.application.domain.DomainTenant;
import com.bacos.mokengeli.biloko.application.domain.DomainUser;
import com.bacos.mokengeli.biloko.application.domain.RoleEnum;
import com.bacos.mokengeli.biloko.application.domain.model.ConnectedUser;
import com.bacos.mokengeli.biloko.application.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Page<DomainTenant> getAllTenants(int page, int size) throws ServiceException {
        ConnectedUser current = userAppService.getConnectedUser();
        if (userAppService.isAdminUser()) {
            return tenantPort.findAllTenantsByTenant(page, size);
        }
        String tenantCode = current.getTenantCode();
        Optional<DomainTenant> tenantByCode = tenantPort.getTenantByCode(tenantCode);
        if (tenantByCode.isPresent()) {
            return new PageImpl<>(
                    Collections.singletonList(tenantByCode.get()),
                    PageRequest.of(page, size),
                    1  // totalElements = 1
            );
        }
        String errorId = UUID.randomUUID().toString();
        log.error("[{}]: No tenant found for User [{}]", errorId, current.getEmployeeNumber());
        throw new ServiceException(errorId, "You are not attach to any tenant. Please contact the support team.");
    }

    public DomainTenant createTenant(DomainTenant domainTenant) throws ServiceException {
        boolean adminUser = this.userAppService.isAdminUser();
        if (!adminUser) {
            RoleEnum mainRole = this.userAppService.getMainRole();
            String employeeNumber = this.userAppService.getConnectedUser().getEmployeeNumber();

            String uuid = UUID.randomUUID().toString();
            log.error("[{}]: User [{}] with role {} try to create a new restaurant",
                    uuid, employeeNumber, mainRole);
            throw new ServiceException(uuid, "You don't have the right to create new restaurant");
        }
        return tenantPort.createNewTenant(domainTenant);
    }
}
