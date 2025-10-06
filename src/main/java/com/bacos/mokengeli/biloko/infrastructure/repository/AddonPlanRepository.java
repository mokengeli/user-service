package com.bacos.mokengeli.biloko.infrastructure.repository;

import com.bacos.mokengeli.biloko.infrastructure.model.AddonPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité AddonPlan.
 * Gère les plans tarifaires disponibles.
 */
@Repository
public interface AddonPlanRepository extends JpaRepository<AddonPlan, Long> {

    /**
     * Récupère tous les plans actifs avec leurs features (JOIN FETCH).
     *
     * @return Liste des plans actifs
     */
    @Query("""
            SELECT DISTINCT ap FROM AddonPlan ap
            LEFT JOIN FETCH ap.features
            WHERE ap.isActive = true
            ORDER BY ap.price ASC
            """)
    List<AddonPlan> findAllActiveWithFeatures();

    /**
     * Récupère tous les plans d'une catégorie spécifique.
     *
     * @param category Catégorie (crm, reservations, onlineOrdering, all)
     * @return Liste des plans de cette catégorie
     */
    @Query("""
            SELECT DISTINCT ap FROM AddonPlan ap
            LEFT JOIN FETCH ap.features
            WHERE ap.category = :category
            AND ap.isActive = true
            ORDER BY ap.price ASC
            """)
    List<AddonPlan> findByCategoryWithFeatures(@Param("category") String category);

    /**
     * Récupère un plan par son ID avec ses features.
     *
     * @param id ID du plan
     * @return Optional de AddonPlan
     */
    @Query("""
            SELECT ap FROM AddonPlan ap
            LEFT JOIN FETCH ap.features
            WHERE ap.id = :id
            """)
    Optional<AddonPlan> findByIdWithFeatures(@Param("id") Long id);

    /**
     * Récupère un plan par son code métier avec ses features.
     *
     * @param code Code métier du plan (ex: "CRM_STARTER", "CRM_PRO")
     * @return Optional de AddonPlan
     */
    @Query("""
            SELECT ap FROM AddonPlan ap
            LEFT JOIN FETCH ap.features
            WHERE ap.code = :code
            """)
    Optional<AddonPlan> findByCodeWithFeatures(@Param("code") String code);

    /**
     * Vérifie si un plan existe et est actif.
     *
     * @param id ID du plan
     * @return true si plan actif existe, false sinon
     */
    boolean existsByIdAndIsActiveTrue(Long id);

    /**
     * Vérifie si un plan existe et est actif par son code.
     *
     * @param code Code métier du plan
     * @return true si plan actif existe, false sinon
     */
    boolean existsByCodeAndIsActiveTrue(String code);

    /**
     * Récupère tous les plans actifs (sans JOIN FETCH).
     *
     * @return Liste des plans actifs
     */
    List<AddonPlan> findByIsActiveTrue();
}
