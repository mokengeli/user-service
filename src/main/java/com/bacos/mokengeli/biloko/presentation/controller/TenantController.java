package com.bacos.mokengeli.biloko.presentation.controller;


import com.bacos.mokengeli.biloko.application.domain.DomainTenant;
import com.bacos.mokengeli.biloko.application.exception.ServiceException;
import com.bacos.mokengeli.biloko.application.service.TenantService;
import com.bacos.mokengeli.biloko.presentation.exception.ResponseStatusWrapperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @PreAuthorize("hasAuthority('CREATE_TENANTS')")
    @PostMapping
    public ResponseEntity<DomainTenant> createTenant(@RequestBody DomainTenant domainTenant) {
        try {
            return ResponseEntity.ok(tenantService.createTenant(domainTenant));
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }

    @PreAuthorize("hasAuthority('VIEW_TENANTS')")
    @GetMapping("/all")
    public ResponseEntity<List<DomainTenant>> getAllTenants() {
        try {
            List<DomainTenant> domainTenants = tenantService.getAllTenantsByOrganisation();
            return ResponseEntity.ok(domainTenants);
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }


    @PreAuthorize("hasAuthority('VIEW_TENANTS')")
    @GetMapping("/all/pg")
    public ResponseEntity<Page<DomainTenant>> getAllTenantsWithPagination(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            Page<DomainTenant> tenants = tenantService.getAllTenants(page, size);
            return ResponseEntity.ok(tenants);
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }

    @PreAuthorize("hasAuthority('VIEW_TENANTS')")
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
