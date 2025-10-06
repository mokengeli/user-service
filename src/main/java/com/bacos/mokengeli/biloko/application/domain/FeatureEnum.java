package com.bacos.mokengeli.biloko.application.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Énumération de toutes les features premium disponibles dans le système d'addons.
 * Chaque feature appartient à un groupe (CRM, Reservations, Online Ordering).
 * Note: le code correspond au champ 'code' de la table features (ex: "basicCRM").
 */
public enum FeatureEnum {
    // Groupe CRM
    BASIC_CRM("basicCRM", "CRM Basique", "Gestion de base de données clients avec historique", AddonCategoryEnum.CRM),
    LOYALTY_PROGRAM("loyaltyProgram", "Programme de fidélité", "Système de points et récompenses", AddonCategoryEnum.CRM),
    EMAIL_MARKETING("emailMarketing", "Email Marketing", "Campagnes email automatisées et segmentation", AddonCategoryEnum.CRM),
    SMS_MARKETING("smsMarketing", "SMS Marketing", "Campagnes SMS ciblées", AddonCategoryEnum.CRM),

    // Groupe Reservations
    TABLE_RESERVATIONS("tableReservations", "Réservations de tables", "Système de réservation en ligne avec planning", AddonCategoryEnum.RESERVATIONS),
    WAITLIST("waitlist", "Liste d'attente", "Gestion de file d'attente digitale", AddonCategoryEnum.RESERVATIONS),
    RESERVATION_REMINDERS("reservationReminders", "Rappels automatiques", "SMS/Email de rappel 24h avant réservation", AddonCategoryEnum.RESERVATIONS),

    // Groupe Online Ordering
    ONLINE_ORDERING("onlineOrdering", "Commandes web", "Site de commande en ligne responsive", AddonCategoryEnum.ONLINE_ORDERING),
    DELIVERY_TRACKING("deliveryTracking", "Suivi de livraison", "Tracking GPS en temps réel", AddonCategoryEnum.ONLINE_ORDERING),
    MENU_MANAGEMENT("menuManagement", "Gestion menu en ligne", "Menu digital synchronisé avec POS", AddonCategoryEnum.ONLINE_ORDERING);

    private final String code;
    private final String label;
    private final String description;
    private final AddonCategoryEnum category;

    FeatureEnum(String code, String label, String description, AddonCategoryEnum category) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.category = category;
    }

    public String getCode() {
        return code;
    }

    /**
     * @deprecated Use getCode() instead for consistency with database schema
     */
    @Deprecated
    public String getId() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public AddonCategoryEnum getCategory() {
        return category;
    }

    public String getGroup() {
        return category.getId();
    }

    /**
     * Recherche une feature par son code.
     * @param code Le code de la feature (ex: "basicCRM")
     * @return L'enum correspondant ou null si non trouvé
     */
    public static FeatureEnum getByCode(String code) {
        if (code == null) return null;
        return Arrays.stream(values())
                .filter(f -> f.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    /**
     * @deprecated Use getByCode() instead for consistency with database schema
     */
    @Deprecated
    public static FeatureEnum getById(String id) {
        return getByCode(id);
    }

    /**
     * Vérifie si un code de feature est valide.
     * @param code Le code à vérifier
     * @return true si la feature existe, false sinon
     */
    public static boolean isValid(String code) {
        return getByCode(code) != null;
    }

    /**
     * Récupère toutes les features d'une catégorie.
     * @param category La catégorie
     * @return Liste des features de cette catégorie
     */
    public static List<FeatureEnum> getByCategory(AddonCategoryEnum category) {
        return Arrays.stream(values())
                .filter(f -> f.getCategory() == category)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les codes de features sous forme de liste.
     * @return Liste des codes (ex: ["basicCRM", "loyaltyProgram", ...])
     */
    public static List<String> getAllCodes() {
        return Arrays.stream(values())
                .map(FeatureEnum::getCode)
                .collect(Collectors.toList());
    }

    /**
     * @deprecated Use getAllCodes() instead for consistency with database schema
     */
    @Deprecated
    public static List<String> getAllIds() {
        return getAllCodes();
    }
}
