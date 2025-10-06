package com.bacos.mokengeli.biloko.application.port;

import com.bacos.mokengeli.biloko.application.domain.DomainAddonPlan;
import com.bacos.mokengeli.biloko.application.domain.DomainAddonRecommendation;
import com.bacos.mokengeli.biloko.application.domain.DomainAddonRequest;
import com.bacos.mokengeli.biloko.application.domain.DomainTenantFeature;
import com.bacos.mokengeli.biloko.application.exception.ServiceException;

import java.util.List;
import java.util.Optional;

/**
 * Port pour la gestion des features tenant et des addons.
 * Interface entre la couche Application et Infrastructure (Hexagonal Architecture).
 */
public interface TenantFeaturePort {

    // === TENANT FEATURES (Activation/Désactivation) ===

    /**
     * Récupère toutes les features activées/désactivées pour un tenant.
     * Utilisé par GET /api/v1/tenant-features/status
     *
     * @param tenantCode Code du tenant
     * @return Liste des features du tenant
     */
    List<DomainTenantFeature> getTenantFeatures(String tenantCode);

    /**
     * Active une feature pour un tenant (Admin uniquement).
     * Utilisé par POST /api/v1/tenant-features/activate
     *
     * @param tenantCode Code du tenant
     * @param featureId ID de la feature à activer
     * @param activatedByEmployeeNumber Employé qui active
     * @return Feature activée
     * @throws ServiceException Si feature déjà activée ou données invalides
     */
    DomainTenantFeature activateFeature(String tenantCode, String featureId, String activatedByEmployeeNumber) throws ServiceException;

    /**
     * Désactive une feature pour un tenant (Admin uniquement).
     * Utilisé par POST /api/v1/tenant-features/deactivate
     *
     * @param tenantCode Code du tenant
     * @param featureId ID de la feature à désactiver
     * @param deactivatedByEmployeeNumber Employé qui désactive
     * @return Feature désactivée
     * @throws ServiceException Si feature déjà désactivée ou données invalides
     */
    DomainTenantFeature deactivateFeature(String tenantCode, String featureId, String deactivatedByEmployeeNumber) throws ServiceException;

    /**
     * Vérifie si une feature est activée pour un tenant.
     *
     * @param tenantCode Code du tenant
     * @param featureId ID de la feature
     * @return true si activée, false sinon
     */
    boolean isFeatureEnabled(String tenantCode, String featureId);

    // === ADDON PLANS ===

    /**
     * Récupère tous les plans actifs disponibles.
     * Utilisé par GET /api/v1/tenant-features/plans
     *
     * @return Liste des plans actifs
     */
    List<DomainAddonPlan> getAllActivePlans();

    /**
     * Récupère les plans d'une catégorie spécifique.
     * Utilisé par GET /api/v1/tenant-features/plans?category=CRM
     *
     * @param category Catégorie (crm, reservations, onlineOrdering)
     * @return Liste des plans de cette catégorie
     */
    List<DomainAddonPlan> getPlansByCategory(String category);

    /**
     * Récupère un plan par son ID.
     *
     * @param planId ID du plan
     * @return Optional du plan
     */
    Optional<DomainAddonPlan> getPlanById(String planId);

    // === ADDON REQUESTS (Manager → Admin Workflow) ===

    /**
     * Crée une demande d'activation d'addon (Manager uniquement).
     * Utilisé par POST /api/v1/tenant-features/request
     *
     * @param request Demande d'activation
     * @return Demande créée avec ticketId généré
     * @throws ServiceException Si demande déjà en attente pour cette feature
     */
    DomainAddonRequest createAddonRequest(DomainAddonRequest request) throws ServiceException;

    /**
     * Récupère toutes les demandes d'un tenant.
     *
     * @param tenantCode Code du tenant
     * @return Liste des demandes
     */
    List<DomainAddonRequest> getRequestsByTenant(String tenantCode);

    /**
     * Récupère toutes les demandes en attente (Admin uniquement).
     *
     * @return Liste des demandes PENDING
     */
    List<DomainAddonRequest> getPendingRequests();

    /**
     * Récupère une demande par son ticketId.
     *
     * @param ticketId Ticket ID (ex: ADDON-2025-001)
     * @return Optional de la demande
     */
    Optional<DomainAddonRequest> getRequestByTicketId(String ticketId);

    /**
     * Approuve une demande d'addon (Admin uniquement).
     * Active automatiquement la feature si demande approuvée.
     *
     * @param ticketId Ticket ID de la demande
     * @param processedByEmployeeNumber Employé qui traite la demande
     * @return Demande approuvée
     * @throws ServiceException Si demande introuvable ou déjà traitée
     */
    DomainAddonRequest approveRequest(String ticketId, String processedByEmployeeNumber) throws ServiceException;

    /**
     * Rejette une demande d'addon (Admin uniquement).
     *
     * @param ticketId Ticket ID de la demande
     * @param processedByEmployeeNumber Employé qui traite la demande
     * @return Demande rejetée
     * @throws ServiceException Si demande introuvable ou déjà traitée
     */
    DomainAddonRequest rejectRequest(String ticketId, String processedByEmployeeNumber) throws ServiceException;

    // === RECOMMENDATIONS ===

    /**
     * Génère des recommandations de plans pour un tenant.
     * Utilisé par GET /api/v1/tenant-features/recommendations
     *
     * @param tenantCode Code du tenant
     * @return Recommandations personnalisées
     */
    DomainAddonRecommendation getRecommendations(String tenantCode);
}
