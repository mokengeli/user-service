package com.bacos.mokengeli.biloko.application.service;

import com.bacos.mokengeli.biloko.application.domain.*;
import com.bacos.mokengeli.biloko.application.domain.model.ConnectedUser;
import com.bacos.mokengeli.biloko.application.exception.ServiceException;
import com.bacos.mokengeli.biloko.application.port.TenantFeaturePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service métier pour la gestion des addons et features tenant.
 * Contient les validations métier et les règles de sécurité (Admin vs Manager, multi-tenant).
 * Les permissions sont gérées au niveau Controller via @PreAuthorize.
 */
@Slf4j
@Service
public class TenantFeatureService {

    private final UserAppService userAppService;
    private final TenantFeaturePort tenantFeaturePort;

    @Autowired
    public TenantFeatureService(UserAppService userAppService, TenantFeaturePort tenantFeaturePort) {
        this.userAppService = userAppService;
        this.tenantFeaturePort = tenantFeaturePort;
    }

    // === TENANT FEATURES (GET /status) ===

    /**
     * Récupère les features d'un tenant.
     * Manager: uniquement son propre tenant.
     * Admin: tous les tenants.
     */
    public List<DomainTenantFeature> getTenantFeatures(String tenantCode) throws ServiceException {
        ConnectedUser connectedUser = userAppService.getConnectedUser();

        // Validation multi-tenant (Manager ne peut voir que son tenant)
        if (!userAppService.isAdminUser() && !tenantCode.equals(connectedUser.getTenantCode())) {
            String errorId = UUID.randomUUID().toString();
            log.error("[{}]: User [{}] tried to access tenant features for [{}] but belongs to [{}]",
                    errorId, connectedUser.getEmployeeNumber(), tenantCode, connectedUser.getTenantCode());
            throw new ServiceException(errorId, "Accès refusé à ce tenant");
        }

        return tenantFeaturePort.getTenantFeatures(tenantCode);
    }

    // === ACTIVATION / DÉSACTIVATION (Admin uniquement) ===

    /**
     * Active une feature pour un tenant (Admin uniquement).
     */
    public DomainTenantFeature activateFeature(String tenantCode, String featureId) throws ServiceException {
        ConnectedUser connectedUser = userAppService.getConnectedUser();

        // Validation Admin uniquement
        if (!userAppService.isAdminUser()) {
            String errorId = UUID.randomUUID().toString();
            log.error("[{}]: User [{}] tried to activate feature but is not admin",
                    errorId, connectedUser.getEmployeeNumber());
            throw new ServiceException(errorId, "Action réservée aux administrateurs");
        }

        return tenantFeaturePort.activateFeature(tenantCode, featureId, connectedUser.getEmployeeNumber());
    }

    /**
     * Désactive une feature pour un tenant (Admin uniquement).
     */
    public DomainTenantFeature deactivateFeature(String tenantCode, String featureId) throws ServiceException {
        ConnectedUser connectedUser = userAppService.getConnectedUser();

        // Validation Admin uniquement
        if (!userAppService.isAdminUser()) {
            String errorId = UUID.randomUUID().toString();
            log.error("[{}]: User [{}] tried to deactivate feature but is not admin",
                    errorId, connectedUser.getEmployeeNumber());
            throw new ServiceException(errorId, "Action réservée aux administrateurs");
        }

        return tenantFeaturePort.deactivateFeature(tenantCode, featureId, connectedUser.getEmployeeNumber());
    }

    // === ADDON PLANS (GET /plans) ===

    /**
     * Récupère tous les plans actifs.
     */
    public List<DomainAddonPlan> getAllActivePlans() {
        return tenantFeaturePort.getAllActivePlans();
    }

    /**
     * Récupère les plans d'une catégorie spécifique.
     */
    public List<DomainAddonPlan> getPlansByCategory(String category) throws ServiceException {
        // Validation de la catégorie
        if (!AddonCategoryEnum.isValid(category)) {
            String errorId = UUID.randomUUID().toString();
            log.error("[{}]: Invalid category: {}", errorId, category);
            throw new ServiceException(errorId, "Catégorie invalide: " + category);
        }

        return tenantFeaturePort.getPlansByCategory(category);
    }

    // === ADDON REQUESTS (Manager → Admin Workflow) ===

    /**
     * Crée une demande d'activation d'addon.
     * Manager peut créer une demande pour son tenant uniquement.
     */
    public DomainAddonRequest createAddonRequest(DomainAddonRequest request) throws ServiceException {
        ConnectedUser connectedUser = userAppService.getConnectedUser();
        String tenantCode = connectedUser.getTenantCode();

        // Validation multi-tenant (Manager ne peut créer que pour son tenant)
        if (!userAppService.isAdminUser() && !tenantCode.equals(request.getTenantCode())) {
            String errorId = UUID.randomUUID().toString();
            log.error("[{}]: User [{}] tried to create request for tenant [{}] but belongs to [{}]",
                    errorId, connectedUser.getEmployeeNumber(), request.getTenantCode(), tenantCode);
            throw new ServiceException(errorId, "Vous ne pouvez créer de demande que pour votre tenant");
        }

        // S'assurer que requestedByEmployeeNumber = connectedUser (sécurité)
        request.setRequestedByEmployeeNumber(connectedUser.getEmployeeNumber());
        request.setTenantCode(tenantCode);

        return tenantFeaturePort.createAddonRequest(request);
    }

    /**
     * Récupère les demandes d'un tenant.
     * Manager: uniquement son tenant.
     * Admin: tous les tenants.
     */
    public List<DomainAddonRequest> getRequestsByTenant(String tenantCode) throws ServiceException {
        ConnectedUser connectedUser = userAppService.getConnectedUser();

        // Validation multi-tenant
        if (!userAppService.isAdminUser() && !tenantCode.equals(connectedUser.getTenantCode())) {
            String errorId = UUID.randomUUID().toString();
            log.error("[{}]: User [{}] tried to access requests for tenant [{}] but belongs to [{}]",
                    errorId, connectedUser.getEmployeeNumber(), tenantCode, connectedUser.getTenantCode());
            throw new ServiceException(errorId, "Accès refusé à ce tenant");
        }

        return tenantFeaturePort.getRequestsByTenant(tenantCode);
    }

    /**
     * Récupère toutes les demandes en attente (Admin uniquement).
     */
    public List<DomainAddonRequest> getPendingRequests() throws ServiceException {
        ConnectedUser connectedUser = userAppService.getConnectedUser();

        // Validation Admin uniquement
        if (!userAppService.isAdminUser()) {
            String errorId = UUID.randomUUID().toString();
            log.error("[{}]: User [{}] tried to get pending requests but is not admin",
                    errorId, connectedUser.getEmployeeNumber());
            throw new ServiceException(errorId, "Action réservée aux administrateurs");
        }

        return tenantFeaturePort.getPendingRequests();
    }

    /**
     * Approuve une demande d'addon (Admin uniquement).
     */
    public DomainAddonRequest approveRequest(String ticketId) throws ServiceException {
        ConnectedUser connectedUser = userAppService.getConnectedUser();

        // Validation Admin uniquement
        if (!userAppService.isAdminUser()) {
            String errorId = UUID.randomUUID().toString();
            log.error("[{}]: User [{}] tried to approve request but is not admin",
                    errorId, connectedUser.getEmployeeNumber());
            throw new ServiceException(errorId, "Action réservée aux administrateurs");
        }

        return tenantFeaturePort.approveRequest(ticketId, connectedUser.getEmployeeNumber());
    }

    /**
     * Rejette une demande d'addon (Admin uniquement).
     */
    public DomainAddonRequest rejectRequest(String ticketId) throws ServiceException {
        ConnectedUser connectedUser = userAppService.getConnectedUser();

        // Validation Admin uniquement
        if (!userAppService.isAdminUser()) {
            String errorId = UUID.randomUUID().toString();
            log.error("[{}]: User [{}] tried to reject request but is not admin",
                    errorId, connectedUser.getEmployeeNumber());
            throw new ServiceException(errorId, "Action réservée aux administrateurs");
        }

        return tenantFeaturePort.rejectRequest(ticketId, connectedUser.getEmployeeNumber());
    }

    // === RECOMMENDATIONS ===

    /**
     * Génère des recommandations de plans pour le tenant de l'utilisateur connecté.
     */
    public DomainAddonRecommendation getRecommendations() {
        ConnectedUser connectedUser = userAppService.getConnectedUser();
        return tenantFeaturePort.getRecommendations(connectedUser.getTenantCode());
    }

    // === UTILITY ===

    /**
     * Vérifie si une feature est activée pour un tenant.
     */
    public boolean isFeatureEnabled(String tenantCode, String featureId) {
        return tenantFeaturePort.isFeatureEnabled(tenantCode, featureId);
    }
}
