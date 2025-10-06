package com.bacos.mokengeli.biloko.infrastructure.repository;

import com.bacos.mokengeli.biloko.infrastructure.model.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Feature.
 * Gère le catalogue des features disponibles.
 */
@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {

    /**
     * Récupère une feature par son code métier.
     *
     * @param code Code métier unique (ex: "basicCRM", "emailMarketing")
     * @return Optional de Feature
     */
    Optional<Feature> findByCode(String code);

    /**
     * Récupère toutes les features actives.
     *
     * @return Liste des features actives
     */
    List<Feature> findByIsActiveTrueOrderByDisplayOrderAsc();

    /**
     * Récupère toutes les features d'une catégorie spécifique.
     *
     * @param category Catégorie (crm, reservations, onlineOrdering)
     * @return Liste des features de cette catégorie
     */
    List<Feature> findByCategoryAndIsActiveTrueOrderByDisplayOrderAsc(String category);

    /**
     * Vérifie si une feature existe et est active par son code.
     *
     * @param code Code métier unique
     * @return true si feature active existe, false sinon
     */
    boolean existsByCodeAndIsActiveTrue(String code);

    /**
     * Récupère toutes les features (actives et inactives).
     *
     * @return Liste de toutes les features
     */
    @Query("SELECT f FROM Feature f ORDER BY f.displayOrder ASC")
    List<Feature> findAllOrderByDisplayOrder();
}
