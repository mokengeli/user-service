package com.bacos.mokengeli.biloko.infrastructure.adapter;

import com.bacos.mokengeli.biloko.application.domain.*;
import com.bacos.mokengeli.biloko.application.exception.ServiceException;
import com.bacos.mokengeli.biloko.application.port.TenantFeaturePort;
import com.bacos.mokengeli.biloko.infrastructure.mapper.AddonPlanMapper;
import com.bacos.mokengeli.biloko.infrastructure.mapper.AddonRequestMapper;
import com.bacos.mokengeli.biloko.infrastructure.mapper.TenantFeatureMapper;
import com.bacos.mokengeli.biloko.infrastructure.model.*;
import com.bacos.mokengeli.biloko.infrastructure.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TenantFeatureAdapter implements TenantFeaturePort {

    private final TenantFeatureRepository tenantFeatureRepository;
    private final AddonRequestRepository addonRequestRepository;
    private final AddonPlanRepository addonPlanRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final FeatureRepository featureRepository;

    @Autowired
    public TenantFeatureAdapter(
            TenantFeatureRepository tenantFeatureRepository,
            AddonRequestRepository addonRequestRepository,
            AddonPlanRepository addonPlanRepository,
            TenantRepository tenantRepository,
            UserRepository userRepository,
            FeatureRepository featureRepository) {
        this.tenantFeatureRepository = tenantFeatureRepository;
        this.addonRequestRepository = addonRequestRepository;
        this.addonPlanRepository = addonPlanRepository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.featureRepository = featureRepository;
    }

    // === TENANT FEATURES ===

    @Override
    public List<DomainTenantFeature> getTenantFeatures(String tenantCode) {
        List<TenantFeature> features = tenantFeatureRepository.findByTenantCodeWithFetch(tenantCode);
        return features.stream()
                .map(TenantFeatureMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public DomainTenantFeature activateFeature(String tenantCode, String featureCode, String activatedByEmployeeNumber) throws ServiceException {
        String errorId = UUID.randomUUID().toString();

        // Validation de la feature
        if (!FeatureEnum.isValid(featureCode)) {
            throw new ServiceException(errorId, "Feature code invalide: " + featureCode);
        }

        // Récupération de la feature
        Feature feature = featureRepository.findByCode(featureCode)
                .orElseThrow(() -> {
                    log.error("{} - Feature introuvable: {}", errorId, featureCode);
                    return new ServiceException(errorId, "Feature introuvable");
                });

        // Récupération du tenant
        Tenant tenant = tenantRepository.findByCode(tenantCode);
        if (tenant == null) {
            log.error("{} - Tenant introuvable: {}", errorId, tenantCode);
            throw new ServiceException(errorId, "Tenant introuvable");
        }

        // Récupération de l'utilisateur
        User activatedBy = userRepository.findByEmployeeNumberAndTenant_Code(activatedByEmployeeNumber, tenantCode);
        if (activatedBy == null) {
            log.error("{} - Utilisateur introuvable: {}", errorId, activatedByEmployeeNumber);
            throw new ServiceException(errorId, "Utilisateur introuvable");
        }

        // Vérifier si la feature existe déjà
        Optional<TenantFeature> existingOpt = tenantFeatureRepository.findByTenant_CodeAndFeature_Code(tenantCode, featureCode);

        TenantFeature tenantFeature;
        OffsetDateTime now = OffsetDateTime.now();

        if (existingOpt.isPresent()) {
            tenantFeature = existingOpt.get();

            if (Boolean.TRUE.equals(tenantFeature.getEnabled())) {
                throw new ServiceException(errorId, "Feature déjà activée pour ce tenant");
            }

            // Réactivation
            tenantFeature.setEnabled(true);
            tenantFeature.setActivatedAt(now);
            tenantFeature.setActivatedBy(activatedBy);
            tenantFeature.setDeactivatedAt(null);
            tenantFeature.setDeactivatedBy(null);
            tenantFeature.setUpdatedAt(now);
        } else {
            // Nouvelle activation
            tenantFeature = TenantFeature.builder()
                    .tenant(tenant)
                    .feature(feature)
                    .enabled(true)
                    .activatedAt(now)
                    .activatedBy(activatedBy)
                    .createdAt(now)
                    .build();
        }

        TenantFeature saved = tenantFeatureRepository.save(tenantFeature);
        log.info("Feature {} activée pour tenant {} par {}", featureCode, tenantCode, activatedByEmployeeNumber);

        return TenantFeatureMapper.toDomain(saved);
    }

    @Override
    public DomainTenantFeature deactivateFeature(String tenantCode, String featureCode, String deactivatedByEmployeeNumber) throws ServiceException {
        String errorId = UUID.randomUUID().toString();

        // Validation de la feature
        if (!FeatureEnum.isValid(featureCode)) {
            throw new ServiceException(errorId, "Feature code invalide: " + featureCode);
        }

        // Récupération de l'utilisateur
        User deactivatedBy = userRepository.findByEmployeeNumberAndTenant_Code(deactivatedByEmployeeNumber, tenantCode);
        if (deactivatedBy == null) {
            log.error("{} - Utilisateur introuvable: {}", errorId, deactivatedByEmployeeNumber);
            throw new ServiceException(errorId, "Utilisateur introuvable");
        }

        // Récupération de la feature
        TenantFeature tenantFeature = tenantFeatureRepository.findByTenant_CodeAndFeature_Code(tenantCode, featureCode)
                .orElseThrow(() -> {
                    log.error("{} - Feature non trouvée: {} pour tenant {}", errorId, featureCode, tenantCode);
                    return new ServiceException(errorId, "Feature non activée pour ce tenant");
                });

        if (Boolean.FALSE.equals(tenantFeature.getEnabled())) {
            throw new ServiceException(errorId, "Feature déjà désactivée pour ce tenant");
        }

        // Désactivation
        OffsetDateTime now = OffsetDateTime.now();
        tenantFeature.setEnabled(false);
        tenantFeature.setDeactivatedAt(now);
        tenantFeature.setDeactivatedBy(deactivatedBy);
        tenantFeature.setUpdatedAt(now);

        TenantFeature saved = tenantFeatureRepository.save(tenantFeature);
        log.info("Feature {} désactivée pour tenant {} par {}", featureCode, tenantCode, deactivatedByEmployeeNumber);

        return TenantFeatureMapper.toDomain(saved);
    }

    @Override
    public boolean isFeatureEnabled(String tenantCode, String featureCode) {
        return tenantFeatureRepository.existsByTenant_CodeAndFeature_CodeAndEnabledTrue(tenantCode, featureCode);
    }

    // === ADDON PLANS ===

    @Override
    public List<DomainAddonPlan> getAllActivePlans() {
        List<AddonPlan> plans = addonPlanRepository.findAllActiveWithFeatures();
        return plans.stream()
                .map(AddonPlanMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DomainAddonPlan> getPlansByCategory(String category) {
        List<AddonPlan> plans = addonPlanRepository.findByCategoryWithFeatures(category);
        return plans.stream()
                .map(AddonPlanMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DomainAddonPlan> getPlanById(String planCode) {
        return addonPlanRepository.findByCodeWithFeatures(planCode)
                .map(AddonPlanMapper::toDomain);
    }

    // === ADDON REQUESTS ===

    @Override
    public DomainAddonRequest createAddonRequest(DomainAddonRequest request) throws ServiceException {
        String errorId = UUID.randomUUID().toString();

        // Validation de la feature
        if (!FeatureEnum.isValid(request.getFeatureCode())) {
            throw new ServiceException(errorId, "Feature code invalide: " + request.getFeatureCode());
        }

        // Récupération de la feature
        Feature feature = featureRepository.findByCode(request.getFeatureCode())
                .orElseThrow(() -> {
                    log.error("{} - Feature introuvable: {}", errorId, request.getFeatureCode());
                    return new ServiceException(errorId, "Feature introuvable");
                });

        // Vérification qu'une demande PENDING n'existe pas déjà
        boolean hasPendingRequest = addonRequestRepository.existsPendingRequestByFeatureCode(
                request.getTenantCode(),
                request.getFeatureCode()
        );

        if (hasPendingRequest) {
            throw new ServiceException(errorId,
                    "Une demande en attente existe déjà pour cette feature");
        }

        // Récupération du tenant
        Tenant tenant = tenantRepository.findByCode(request.getTenantCode());
        if (tenant == null) {
            log.error("{} - Tenant introuvable: {}", errorId, request.getTenantCode());
            throw new ServiceException(errorId, "Tenant introuvable");
        }

        // Récupération de l'utilisateur demandeur
        User requestedBy = userRepository.findByEmployeeNumberAndTenant_Code(
                request.getRequestedByEmployeeNumber(),
                request.getTenantCode()
        );
        if (requestedBy == null) {
            log.error("{} - Utilisateur introuvable: {}", errorId, request.getRequestedByEmployeeNumber());
            throw new ServiceException(errorId, "Utilisateur introuvable");
        }

        // Génération du ticket ID (format: ADDON-YYYY-NNN)
        String ticketId = generateTicketId();

        // Création de l'entité
        AddonRequest entity = AddonRequest.builder()
                .ticketId(ticketId)
                .tenant(tenant)
                .feature(feature)
                .requestedBy(requestedBy)
                .message(request.getMessage())
                .status(AddonRequestStatusEnum.PENDING.getCode())
                .createdAt(OffsetDateTime.now())
                .build();

        AddonRequest saved = addonRequestRepository.save(entity);
        log.info("Demande d'addon créée: {} pour tenant {} par {}",
                ticketId, request.getTenantCode(), request.getRequestedByEmployeeNumber());

        return AddonRequestMapper.toDomain(saved);
    }

    @Override
    public List<DomainAddonRequest> getRequestsByTenant(String tenantCode) {
        List<AddonRequest> requests = addonRequestRepository.findByTenantCodeWithFetch(tenantCode);
        return requests.stream()
                .map(AddonRequestMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DomainAddonRequest> getPendingRequests() {
        List<AddonRequest> requests = addonRequestRepository.findPendingRequests();
        return requests.stream()
                .map(AddonRequestMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DomainAddonRequest> getRequestByTicketId(String ticketId) {
        return addonRequestRepository.findByTicketId(ticketId)
                .map(AddonRequestMapper::toDomain);
    }

    @Override
    public DomainAddonRequest approveRequest(String ticketId, String processedByEmployeeNumber) throws ServiceException {
        String errorId = UUID.randomUUID().toString();

        // Récupération de la demande
        AddonRequest request = addonRequestRepository.findByTicketId(ticketId)
                .orElseThrow(() -> {
                    log.error("{} - Demande introuvable: {}", errorId, ticketId);
                    return new ServiceException(errorId, "Demande introuvable");
                });

        // Vérification du statut
        if (!AddonRequestStatusEnum.PENDING.getCode().equals(request.getStatus())) {
            throw new ServiceException(errorId, "La demande a déjà été traitée");
        }

        // Récupération de l'utilisateur (admin)
        User processedBy = userRepository.findByEmployeeNumber(processedByEmployeeNumber)
                .orElseThrow(() -> {
                    log.error("{} - Utilisateur introuvable: {}", errorId, processedByEmployeeNumber);
                    return new ServiceException(errorId, "Utilisateur introuvable");
                });

        // Mise à jour de la demande
        OffsetDateTime now = OffsetDateTime.now();
        request.setStatus(AddonRequestStatusEnum.APPROVED.getCode());
        request.setProcessedBy(processedBy);
        request.setProcessedAt(now);

        AddonRequest saved = addonRequestRepository.save(request);
        log.info("Demande {} approuvée par {}", ticketId, processedByEmployeeNumber);

        // Activation automatique de la feature
        try {
            activateFeature(
                    request.getTenant().getCode(),
                    request.getFeature().getCode(),
                    processedByEmployeeNumber
            );
        } catch (ServiceException e) {
            // Si la feature est déjà activée, on ignore l'erreur
            log.warn("Erreur lors de l'activation automatique: {}", e.getMessage());
        }

        return AddonRequestMapper.toDomain(saved);
    }

    @Override
    public DomainAddonRequest rejectRequest(String ticketId, String processedByEmployeeNumber) throws ServiceException {
        String errorId = UUID.randomUUID().toString();

        // Récupération de la demande
        AddonRequest request = addonRequestRepository.findByTicketId(ticketId)
                .orElseThrow(() -> {
                    log.error("{} - Demande introuvable: {}", errorId, ticketId);
                    return new ServiceException(errorId, "Demande introuvable");
                });

        // Vérification du statut
        if (!AddonRequestStatusEnum.PENDING.getCode().equals(request.getStatus())) {
            throw new ServiceException(errorId, "La demande a déjà été traitée");
        }

        // Récupération de l'utilisateur (admin)
        User processedBy = userRepository.findByEmployeeNumber(processedByEmployeeNumber)
                .orElseThrow(() -> {
                    log.error("{} - Utilisateur introuvable: {}", errorId, processedByEmployeeNumber);
                    return new ServiceException(errorId, "Utilisateur introuvable");
                });

        // Mise à jour de la demande
        OffsetDateTime now = OffsetDateTime.now();
        request.setStatus(AddonRequestStatusEnum.REJECTED.getCode());
        request.setProcessedBy(processedBy);
        request.setProcessedAt(now);

        AddonRequest saved = addonRequestRepository.save(request);
        log.info("Demande {} rejetée par {}", ticketId, processedByEmployeeNumber);

        return AddonRequestMapper.toDomain(saved);
    }

    // === RECOMMENDATIONS ===

    @Override
    public DomainAddonRecommendation getRecommendations(String tenantCode) {
        // Logique simple pour la V1: recommander tous les plans
        // TODO: Améliorer avec une vraie logique de recommandation basée sur l'usage

        List<DomainAddonPlan> allPlans = getAllActivePlans();

        // Recommander le premier plan actif (logique simple pour l'instant)
        DomainAddonPlan recommendedPlan = allPlans.isEmpty() ? null : allPlans.get(0);

        return DomainAddonRecommendation.builder()
                .tenantCode(tenantCode)
                .planCode(recommendedPlan != null ? recommendedPlan.getCode() : null)
                .reason("Plan recommandé pour optimiser votre utilisation")
                .discount(10) // 10% de réduction
                .validUntil(OffsetDateTime.now().plusMonths(1))
                .build();
    }

    // === PRIVATE HELPERS ===

    /**
     * Génère un ticket ID unique au format ADDON-YYYY-NNN.
     * Exemple: ADDON-2025-001
     */
    private String generateTicketId() {
        int year = OffsetDateTime.now().getYear();
        long count = addonRequestRepository.count() + 1;
        String sequence = String.format("%03d", count);
        return String.format("ADDON-%d-%s", year, sequence);
    }
}
