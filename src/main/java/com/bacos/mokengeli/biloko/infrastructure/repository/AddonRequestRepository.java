package com.bacos.mokengeli.biloko.infrastructure.repository;

import com.bacos.mokengeli.biloko.infrastructure.model.AddonRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité AddonRequest.
 * Gère les demandes d'activation d'addons par les managers.
 */
@Repository
public interface AddonRequestRepository extends JpaRepository<AddonRequest, Long> {

    /**
     * Recherche une demande par son ticket ID.
     *
     * @param ticketId ID du ticket (ex: ADDON-2025-001)
     * @return Optional de AddonRequest
     */
    Optional<AddonRequest> findByTicketId(String ticketId);

    /**
     * Récupère toutes les demandes d'un tenant avec JOIN FETCH.
     *
     * @param tenantCode Code du tenant
     * @return Liste des demandes
     */
    @Query("""
            SELECT ar FROM AddonRequest ar
            LEFT JOIN FETCH ar.tenant
            LEFT JOIN FETCH ar.feature
            LEFT JOIN FETCH ar.requestedBy
            LEFT JOIN FETCH ar.processedBy
            WHERE ar.tenant.code = :tenantCode
            ORDER BY ar.createdAt DESC
            """)
    List<AddonRequest> findByTenantCodeWithFetch(@Param("tenantCode") String tenantCode);

    /**
     * Récupère toutes les demandes selon leur statut.
     *
     * @param status Statut des demandes (PENDING, APPROVED, REJECTED)
     * @return Liste des demandes
     */
    @Query("""
            SELECT ar FROM AddonRequest ar
            LEFT JOIN FETCH ar.tenant
            LEFT JOIN FETCH ar.feature
            LEFT JOIN FETCH ar.requestedBy
            WHERE ar.status = :status
            ORDER BY ar.createdAt DESC
            """)
    List<AddonRequest> findByStatusWithFetch(@Param("status") String status);

    /**
     * Récupère toutes les demandes en attente (PENDING).
     *
     * @return Liste des demandes en attente
     */
    @Query("""
            SELECT ar FROM AddonRequest ar
            LEFT JOIN FETCH ar.tenant
            LEFT JOIN FETCH ar.feature
            LEFT JOIN FETCH ar.requestedBy
            WHERE ar.status = 'PENDING'
            ORDER BY ar.createdAt ASC
            """)
    List<AddonRequest> findPendingRequests();

    /**
     * Vérifie si une demande PENDING existe déjà pour un tenant et une feature (par feature ID).
     *
     * @param tenantCode Code du tenant
     * @param featureId ID technique de la feature
     * @return true si demande PENDING existe, false sinon
     */
    @Query("""
            SELECT COUNT(ar) > 0 FROM AddonRequest ar
            WHERE ar.tenant.code = :tenantCode
            AND ar.feature.id = :featureId
            AND ar.status = 'PENDING'
            """)
    boolean existsPendingRequestByFeatureId(@Param("tenantCode") String tenantCode, @Param("featureId") Long featureId);

    /**
     * Vérifie si une demande PENDING existe déjà pour un tenant et une feature (par feature code).
     *
     * @param tenantCode Code du tenant
     * @param featureCode Code métier de la feature
     * @return true si demande PENDING existe, false sinon
     */
    @Query("""
            SELECT COUNT(ar) > 0 FROM AddonRequest ar
            WHERE ar.tenant.code = :tenantCode
            AND ar.feature.code = :featureCode
            AND ar.status = 'PENDING'
            """)
    boolean existsPendingRequestByFeatureCode(@Param("tenantCode") String tenantCode, @Param("featureCode") String featureCode);

    /**
     * Compte le nombre total de demandes (pour génération du ticket ID).
     *
     * @return Nombre total de demandes
     */
    long count();
}
