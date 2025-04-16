package com.bacos.mokengeli.biloko.presentation.controller;


import com.bacos.mokengeli.biloko.application.domain.DomainTenant;
import com.bacos.mokengeli.biloko.application.exception.ServiceException;
import com.bacos.mokengeli.biloko.application.service.TenantService;
import com.bacos.mokengeli.biloko.presentation.exception.ResponseStatusWrapperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/tenant")
public class TenantController {

    private final TenantService tenantService;

    @Autowired
    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PreAuthorize("hasAuthority('VIEW_TENANT')")
    @GetMapping("/all")
    public ResponseEntity<List<DomainTenant>> getAllTenants() {
        try {
            List<DomainTenant> domainTenants = tenantService.getAllTenantsByOrganisation();
            return ResponseEntity.ok(domainTenants);
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }

    @PreAuthorize("hasAuthority('VIEW_TENANT')")
    @GetMapping("")
    public ResponseEntity<DomainTenant> getTenantsByTenantCode(@RequestParam("code") String tenantCode) {
        try {
            DomainTenant domainTenant = tenantService.getTenantByCode(tenantCode);
            return ResponseEntity.ok(domainTenant);
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }
}
