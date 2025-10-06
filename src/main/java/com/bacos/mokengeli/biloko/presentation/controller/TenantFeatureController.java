package com.bacos.mokengeli.biloko.presentation.controller;

import com.bacos.mokengeli.biloko.application.domain.*;
import com.bacos.mokengeli.biloko.application.exception.ServiceException;
import com.bacos.mokengeli.biloko.application.service.TenantFeatureService;
import com.bacos.mokengeli.biloko.presentation.dto.AddonPlanResponse;
import com.bacos.mokengeli.biloko.presentation.dto.AddonRecommendationResponse;
import com.bacos.mokengeli.biloko.presentation.dto.AddonStatusResponse;
import com.bacos.mokengeli.biloko.presentation.exception.ResponseStatusWrapperException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller REST pour la gestion des addons et features tenant.
 * Base path: /api/v1/tenant-features
 */
@RestController
@RequestMapping("/api/user/tenant-features")
public class TenantFeatureController {

    private final TenantFeatureService tenantFeatureService;

    @Autowired
    public TenantFeatureController(TenantFeatureService tenantFeatureService) {
        this.tenantFeatureService = tenantFeatureService;
    }

    // === GET /status - Récupérer les features activées/désactivées d'un tenant ===

    /**
     * Récupère toutes les features (activées/désactivées) pour un tenant.
     * Retourne un format groupé par catégorie avec featureId + featureName.
     * Accessible par MANAGER (son tenant uniquement) et ADMIN (tous tenants).
     *
     * @param tenantCode Code du tenant
     * @return Statut groupé par catégorie
     */
    @PreAuthorize("hasAuthority('MANAGE_TENANT_ADDONS')")
    @GetMapping("/status")
    public ResponseEntity<AddonStatusResponse> getAddonStatus(
            @RequestParam("tenantCode") String tenantCode) {
        try {
            // Récupérer features activées depuis le service (Domain)
            List<DomainTenantFeature> dbFeatures = tenantFeatureService.getTenantFeatures(tenantCode);

            // Créer Map pour lookup rapide
            Map<String, DomainTenantFeature> featureMap = new HashMap<>();
            for (DomainTenantFeature tf : dbFeatures) {
                featureMap.put(tf.getFeatureCode(), tf);
            }

            // Construire les groupes par catégorie
            Map<String, AddonStatusResponse.GroupFeatures> groups = new LinkedHashMap<>();

            for (AddonCategoryEnum category : AddonCategoryEnum.values()) {
                List<FeatureEnum> categoryFeatures = FeatureEnum.getByCategory(category);

                Map<String, AddonStatusResponse.FeatureDetails> features = new LinkedHashMap<>();
                for (FeatureEnum featureEnum : categoryFeatures) {
                    DomainTenantFeature dbFeature = featureMap.get(featureEnum.getCode());

                    features.put(featureEnum.getCode(), AddonStatusResponse.FeatureDetails.builder()
                        .featureCode(featureEnum.getCode())
                        .featureName(featureEnum.getLabel())
                        .enabled(dbFeature != null && Boolean.TRUE.equals(dbFeature.getEnabled()))
                        .activatedAt(dbFeature != null ? dbFeature.getActivatedAt() : null)
                        .build());
                }

                groups.put(category.getId(), AddonStatusResponse.GroupFeatures.builder()
                    .features(features)
                    .build());
            }

            // Liste des features activées
            List<String> activatedFeatures = dbFeatures.stream()
                .filter(tf -> Boolean.TRUE.equals(tf.getEnabled()))
                .map(DomainTenantFeature::getFeatureCode)
                .collect(Collectors.toList());

            AddonStatusResponse response = AddonStatusResponse.builder()
                .activatedFeatures(activatedFeatures)
                .groups(groups)
                .build();

            return ResponseEntity.ok(response);
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }

    // === POST /activate - Activer une feature (Admin uniquement) ===

    /**
     * Active une feature pour un tenant (Admin uniquement).
     *
     * @param request Requête d'activation
     * @return Feature activée
     */
    @PreAuthorize("hasAuthority('MANAGE_TENANT_ADDONS')")
    @PostMapping("/activate")
    public ResponseEntity<DomainTenantFeature> activateFeature(@RequestBody ActivateFeatureRequest request) {
        try {
            DomainTenantFeature feature = tenantFeatureService.activateFeature(
                    request.getTenantCode(),
                    request.getFeatureCode()
            );
            return ResponseEntity.ok(feature);
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }

    // === POST /deactivate - Désactiver une feature (Admin uniquement) ===

    /**
     * Désactive une feature pour un tenant (Admin uniquement).
     *
     * @param request Requête de désactivation
     * @return Feature désactivée
     */
    @PreAuthorize("hasAuthority('MANAGE_TENANT_ADDONS')")
    @PostMapping("/deactivate")
    public ResponseEntity<DomainTenantFeature> deactivateFeature(@RequestBody DeactivateFeatureRequest request) {
        try {
            DomainTenantFeature feature = tenantFeatureService.deactivateFeature(
                    request.getTenantCode(),
                    request.getFeatureCode()
            );
            return ResponseEntity.ok(feature);
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }

    // === GET /plans - Récupérer les plans disponibles ===

    /**
     * Récupère tous les plans actifs disponibles au format enrichi.
     * Si category est fourni, filtre par catégorie (crm, reservations, onlineOrdering, all).
     * Retourne includedFeatures avec {featureId, featureName}.
     *
     * @param category Catégorie optionnelle
     * @return Liste des plans formatés
     */
    @PreAuthorize("hasAuthority('MANAGE_TENANT_ADDONS')")
    @GetMapping("/plans")
    public ResponseEntity<List<AddonPlanResponse>> getPlans(
            @RequestParam(value = "category", required = false) String category) {
        try {
            // Récupérer les plans depuis le service (Domain)
            List<DomainAddonPlan> domainPlans;
            if (category != null && !category.trim().isEmpty()) {
                domainPlans = tenantFeatureService.getPlansByCategory(category);
            } else {
                domainPlans = tenantFeatureService.getAllActivePlans();
            }

            // Transformer en DTOs enrichis
            List<AddonPlanResponse> plans = domainPlans.stream()
                .map(this::mapToAddonPlanResponse)
                .collect(Collectors.toList());

            return ResponseEntity.ok(plans);
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }

    // === POST /request - Créer une demande d'activation (Manager) ===

    /**
     * Crée une demande d'activation d'addon.
     * Manager peut créer une demande pour son tenant uniquement.
     *
     * @param request Demande d'activation
     * @return Demande créée avec ticketId généré
     */
    @PreAuthorize("hasAuthority('MANAGE_TENANT_ADDONS')")
    @PostMapping("/request")
    public ResponseEntity<DomainAddonRequest> createAddonRequest(@RequestBody DomainAddonRequest request) {
        try {
            DomainAddonRequest created = tenantFeatureService.createAddonRequest(request);
            return ResponseEntity.ok(created);
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }

    // === GET /requests - Récupérer les demandes d'un tenant ===

    /**
     * Récupère toutes les demandes d'un tenant.
     * Manager: uniquement son tenant.
     * Admin: tous les tenants.
     *
     * @param tenantCode Code du tenant
     * @return Liste des demandes
     */
    @PreAuthorize("hasAuthority('MANAGE_TENANT_ADDONS')")
    @GetMapping("/requests")
    public ResponseEntity<List<DomainAddonRequest>> getRequestsByTenant(
            @RequestParam("tenantCode") String tenantCode) {
        try {
            List<DomainAddonRequest> requests = tenantFeatureService.getRequestsByTenant(tenantCode);
            return ResponseEntity.ok(requests);
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }

    // === GET /requests/pending - Récupérer toutes les demandes en attente (Admin) ===

    /**
     * Récupère toutes les demandes en attente (PENDING).
     * Admin uniquement.
     *
     * @return Liste des demandes PENDING
     */
    @PreAuthorize("hasAuthority('MANAGE_TENANT_ADDONS')")
    @GetMapping("/requests/pending")
    public ResponseEntity<List<DomainAddonRequest>> getPendingRequests() {
        try {
            List<DomainAddonRequest> requests = tenantFeatureService.getPendingRequests();
            return ResponseEntity.ok(requests);
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }

    // === POST /requests/{ticketId}/approve - Approuver une demande (Admin) ===

    /**
     * Approuve une demande d'addon et active automatiquement la feature.
     * Admin uniquement.
     *
     * @param ticketId Ticket ID de la demande
     * @return Demande approuvée
     */
    @PreAuthorize("hasAuthority('MANAGE_TENANT_ADDONS')")
    @PostMapping("/requests/{ticketId}/approve")
    public ResponseEntity<DomainAddonRequest> approveRequest(@PathVariable String ticketId) {
        try {
            DomainAddonRequest approved = tenantFeatureService.approveRequest(ticketId);
            return ResponseEntity.ok(approved);
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }

    // === POST /requests/{ticketId}/reject - Rejeter une demande (Admin) ===

    /**
     * Rejette une demande d'addon.
     * Admin uniquement.
     *
     * @param ticketId Ticket ID de la demande
     * @return Demande rejetée
     */
    @PreAuthorize("hasAuthority('MANAGE_TENANT_ADDONS')")
    @PostMapping("/requests/{ticketId}/reject")
    public ResponseEntity<DomainAddonRequest> rejectRequest(@PathVariable String ticketId) {
        try {
            DomainAddonRequest rejected = tenantFeatureService.rejectRequest(ticketId);
            return ResponseEntity.ok(rejected);
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }

    // === GET /recommendations - Récupérer les recommandations ===

    /**
     * Génère des recommandations de plans pour le tenant de l'utilisateur connecté.
     *
     * @param tenantCode Code du tenant
     * @return Recommandations personnalisées
     */
    @PreAuthorize("hasAuthority('MANAGE_TENANT_ADDONS')")
    @GetMapping("/recommendations")
    public ResponseEntity<AddonRecommendationResponse> getRecommendations(
            @RequestParam("tenantCode") String tenantCode) {
        // Récupérer la recommandation du service (Domain)
        DomainAddonRecommendation domain = tenantFeatureService.getRecommendations();

        // Récupérer le plan recommandé pour obtenir son nom
        DomainAddonPlan recommendedPlan = tenantFeatureService.getAllActivePlans().stream()
            .filter(p -> p.getCode().equals(domain.getPlanCode()))
            .findFirst()
            .orElse(null);

        // Transformer en DTO avec une liste d'un seul élément
        List<AddonRecommendationResponse.RecommendationItem> recommendedPlans = new ArrayList<>();
        if (recommendedPlan != null) {
            recommendedPlans.add(AddonRecommendationResponse.RecommendationItem.builder()
                .planCode(domain.getPlanCode())
                .planName(recommendedPlan.getName())
                .reason(domain.getReason() != null ? domain.getReason() : "Recommandé pour vous")
                .build());
        }

        AddonRecommendationResponse response = AddonRecommendationResponse.builder()
            .tenantCode(domain.getTenantCode())
            .discount(domain.getDiscount() != null ? domain.getDiscount().doubleValue() : null)
            .validUntil(domain.getValidUntil())
            .recommendedPlans(recommendedPlans)
            .build();

        return ResponseEntity.ok(response);
    }

    // === HELPER METHODS ===

    /**
     * Mappe un DomainAddonPlan vers AddonPlanResponse.
     * Enrichit les featureIds avec les featureName.
     *
     * @param plan Plan domain
     * @return Plan response DTO
     */
    private AddonPlanResponse mapToAddonPlanResponse(DomainAddonPlan plan) {
        // Transformer includedFeatures (List<String>) en objets {featureId, featureName}
        List<AddonPlanResponse.FeatureInfo> includedFeatures = plan.getIncludedFeatures().stream()
            .map(featureCode -> {
                FeatureEnum featureEnum = FeatureEnum.getByCode(featureCode);
                return AddonPlanResponse.FeatureInfo.builder()
                    .featureCode(featureCode)
                    .featureName(featureEnum != null ? featureEnum.getLabel() : featureCode)
                    .build();
            })
            .collect(Collectors.toList());

        return AddonPlanResponse.builder()
            .code(plan.getCode())
            .name(plan.getName())
            .description(plan.getDescription())
            .price(plan.getPrice())
            .currency(plan.getCurrency())
            .billingPeriod(plan.getBillingPeriod())
            .category(plan.getCategory())
            .savings(plan.getSavings())
            .isActive(plan.getIsActive())
            .includedFeatures(includedFeatures)
            .createdAt(plan.getCreatedAt())
            .updatedAt(plan.getUpdatedAt())
            .build();
    }

    // === DTOs pour les requêtes ===

    @Data
    public static class ActivateFeatureRequest {
        private String tenantCode;
        private String featureCode;
    }

    @Data
    public static class DeactivateFeatureRequest {
        private String tenantCode;
        private String featureCode;
    }
}
