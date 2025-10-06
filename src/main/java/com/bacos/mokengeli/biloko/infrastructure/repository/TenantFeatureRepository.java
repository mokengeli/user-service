package com.bacos.mokengeli.biloko.infrastructure.repository;

import com.bacos.mokengeli.biloko.infrastructure.model.TenantFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité TenantFeature.
 * Gère les features activées/désactivées par tenant.
 */
@Repository
public interface TenantFeatureRepository extends JpaRepository<TenantFeature, Long> {

    /**
     * Récupère toutes les features d'un tenant par son code (avec JOIN FETCH pour optimisation).
     * Query optimisée pour GET /status (appelé très fréquemment).
     *
     * @param tenantCode Code du tenant
     * @return Liste des features du tenant
     */
    @Query("""
            SELECT tf FROM TenantFeature tf
            LEFT JOIN FETCH tf.tenant t
            LEFT JOIN FETCH tf.feature f
            LEFT JOIN FETCH tf.activatedBy
            LEFT JOIN FETCH tf.deactivatedBy
            WHERE t.code = :tenantCode
            """)
    List<TenantFeature> findByTenantCodeWithFetch(@Param("tenantCode") String tenantCode);

    /**
     * Recherche une feature spécifique pour un tenant par feature ID (BIGINT).
     *
     * @param tenantCode Code du tenant
     * @param featureId ID technique de la feature
     * @return Optional de TenantFeature
     */
    Optional<TenantFeature> findByTenant_CodeAndFeature_Id(String tenantCode, Long featureId);

    /**
     * Recherche une feature spécifique pour un tenant par feature code.
     *
     * @param tenantCode Code du tenant
     * @param featureCode Code métier de la feature (ex: "basicCRM")
     * @return Optional de TenantFeature
     */
    Optional<TenantFeature> findByTenant_CodeAndFeature_Code(String tenantCode, String featureCode);

    /**
     * Récupère uniquement les features activées d'un tenant.
     *
     * @param tenantCode Code du tenant
     * @return Liste des features activées
     */
    @Query("""
            SELECT tf FROM TenantFeature tf
            WHERE tf.tenant.code = :tenantCode
            AND tf.enabled = true
            """)
    List<TenantFeature> findActivatedByTenantCode(@Param("tenantCode") String tenantCode);

    /**
     * Vérifie si une feature existe pour un tenant (par feature ID).
     *
     * @param tenantCode Code du tenant
     * @param featureId ID technique de la feature
     * @return true si existe, false sinon
     */
    boolean existsByTenant_CodeAndFeature_Id(String tenantCode, Long featureId);

    /**
     * Vérifie si une feature existe pour un tenant (par feature code).
     *
     * @param tenantCode Code du tenant
     * @param featureCode Code métier de la feature
     * @return true si existe, false sinon
     */
    boolean existsByTenant_CodeAndFeature_Code(String tenantCode, String featureCode);

    /**
     * Vérifie si une feature est activée pour un tenant (par feature ID).
     *
     * @param tenantCode Code du tenant
     * @param featureId ID technique de la feature
     * @return true si activée, false sinon
     */
    boolean existsByTenant_CodeAndFeature_IdAndEnabledTrue(String tenantCode, Long featureId);

    /**
     * Vérifie si une feature est activée pour un tenant (par feature code).
     *
     * @param tenantCode Code du tenant
     * @param featureCode Code métier de la feature
     * @return true si activée, false sinon
     */
    boolean existsByTenant_CodeAndFeature_CodeAndEnabledTrue(String tenantCode, String featureCode);

    /**
     * Supprime toutes les features d'un tenant (pour tests uniquement).
     *
     * @param tenantCode Code du tenant
     */
    void deleteByTenant_Code(String tenantCode);
}
