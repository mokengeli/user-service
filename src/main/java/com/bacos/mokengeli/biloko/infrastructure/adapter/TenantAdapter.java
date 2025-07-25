package com.bacos.mokengeli.biloko.infrastructure.adapter;

import com.bacos.mokengeli.biloko.application.domain.DomainTenant;
import com.bacos.mokengeli.biloko.application.domain.EstablishmentTypeEnum;
import com.bacos.mokengeli.biloko.application.domain.SubscriptionPlanEnum;
import com.bacos.mokengeli.biloko.application.exception.ServiceException;
import com.bacos.mokengeli.biloko.application.service.TenantPort;
import com.bacos.mokengeli.biloko.infrastructure.mapper.TenantMapper;
import com.bacos.mokengeli.biloko.infrastructure.model.EstablishmentType;
import com.bacos.mokengeli.biloko.infrastructure.model.SubscriptionPlan;
import com.bacos.mokengeli.biloko.infrastructure.model.Tenant;
import com.bacos.mokengeli.biloko.infrastructure.repository.EstablishmentTypeRepository;
import com.bacos.mokengeli.biloko.infrastructure.repository.SubscriptionPlanRepository;
import com.bacos.mokengeli.biloko.infrastructure.repository.TenantRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Component
public class TenantAdapter implements TenantPort {
    private final TenantRepository tenantRepository;
    private final EstablishmentTypeRepository establishmentTypeRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private static final int SLUG_LENGTH = 3;
    private static final int SUFFIX_LENGTH = 4;
    private static final int MAX_ATTEMPTS = 5;

    @Autowired
    public TenantAdapter(TenantRepository tenantRepository, EstablishmentTypeRepository establishmentTypeRepository, SubscriptionPlanRepository subscriptionPlanRepository) {
        this.tenantRepository = tenantRepository;
        this.establishmentTypeRepository = establishmentTypeRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
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
        return Optional.of(TenantMapper.toDomain(tenant));
    }


    @Override
    public Page<DomainTenant> findAllTenantsByTenant(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        if (search == null || search.trim().isEmpty()) {
            return tenantRepository
                    .findAll(pageable)
                    .map(TenantMapper::toDomain);
        }
        Page<Tenant> filtered = tenantRepository
                .findByNameContainingIgnoreCase(search, pageable);
        return filtered.map(TenantMapper::toDomain);
    }

    @Override
    public DomainTenant createNewTenant(DomainTenant domainTenant) throws ServiceException {
        String errorId = UUID.randomUUID().toString();
        String code = retrieveTenantCode(domainTenant.getName());
        domainTenant.setCode(code);
        if (tenantRepository.existsByCode(domainTenant.getCode())) {
            throw new ServiceException(errorId,
                    "Le code \"" + domainTenant.getCode() + "\" existe déjà.");
        }
        if (tenantRepository.existsByName(domainTenant.getName())) {
            throw new ServiceException(errorId,
                    "Le nom \"" + domainTenant.getName() + "\" existe déjà.");
        }
        if (tenantRepository.existsByEmail(domainTenant.getEmail())) {
            throw new ServiceException(errorId,
                    "L'email \"" + domainTenant.getEmail() + "\" existe déjà.");
        }

        EstablishmentType establishmentType = this.establishmentTypeRepository.findByCode(EstablishmentTypeEnum.
                        RESTAURANT.name())
                .orElseThrow(() -> {
                    log.error(errorId, "Le type \"" + EstablishmentTypeEnum.RESTAURANT.name() + " n'existe pas");
                    return new ServiceException(errorId, "Une erreur intendue s'est produite");
                });
        SubscriptionPlan subscriptionPlan = this.subscriptionPlanRepository.findByCode(SubscriptionPlanEnum.STARTER.name())
                .orElseThrow(() -> {
                    log.error(errorId, "Le type \"" + EstablishmentTypeEnum.RESTAURANT.name() + " n'existe pas");
                    return new ServiceException(errorId, "Une erreur intendue s'est produite");
                });
        Tenant tenant = TenantMapper.toEntity(domainTenant);
        OffsetDateTime now = OffsetDateTime.now();
        tenant.setCreatedAt(now);
        tenant.setEstablishmentType(establishmentType);
        tenant.setSubscriptionPlan(subscriptionPlan);
        tenant.setUpdatedAt(now);
        Tenant saved = tenantRepository.save(tenant);
        return TenantMapper.toDomain(saved);
    }

    private String retrieveTenantCode(String tenantName) throws ServiceException {
        // 1) Slug complet : minuscules, [a–z0–9] et "-"
        String fullSlug = tenantName.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        // 2) Pool de caractères valides (sans "-")
        String pool = fullSlug.replace("-", "");
        if (pool.length() < SLUG_LENGTH) {
            throw new ServiceException(
                    "SLUG_TOO_SHORT",
                    "Le nom doit contenir au moins " + SLUG_LENGTH + " caractères alphanumériques");
        }

        Random rnd = new Random();
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            // 3) Tirage aléatoire de SLUG_LENGTH caractères
            StringBuilder base = new StringBuilder(SLUG_LENGTH);
            for (int i = 0; i < SLUG_LENGTH; i++) {
                base.append(pool.charAt(rnd.nextInt(pool.length())));
            }

            // 4) Génération du suffixe
            String suffix = RandomStringUtils.randomAlphanumeric(SUFFIX_LENGTH).toLowerCase();
            String code = base + "-" + suffix;

            // 5) Vérification collision
            if (!tenantRepository.existsByCode(code)) {
                return code;
            }
        }

        // 6) Échec après MAX_ATTEMPTS
        throw new ServiceException(
                "CODE_GEN_FAIL",
                "Impossible de générer un code unique après "
                        + MAX_ATTEMPTS + " tentatives");
    }
}
