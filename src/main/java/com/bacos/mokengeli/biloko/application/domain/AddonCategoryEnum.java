package com.bacos.mokengeli.biloko.application.domain;

/**
 * Énumération des catégories d'addons premium.
 * Utilisé pour regrouper les features et les plans.
 */
public enum AddonCategoryEnum {
    CRM("crm", "CRM & Fidélité", "Gestion de la relation client et programmes de fidélité"),
    RESERVATIONS("reservations", "Réservations", "Système de réservation de tables en ligne"),
    ONLINE_ORDERING("onlineOrdering", "Commandes en ligne", "Système de commande en ligne et livraison"),
    ALL("all", "Pack Complet", "Tous les modules réunis");

    private final String id;
    private final String label;
    private final String description;

    AddonCategoryEnum(String id, String label, String description) {
        this.id = id;
        this.label = label;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Recherche une catégorie par son ID.
     * @param id L'identifiant de la catégorie (ex: "crm")
     * @return L'enum correspondant ou null si non trouvé
     */
    public static AddonCategoryEnum getById(String id) {
        if (id == null) return null;
        for (AddonCategoryEnum category : values()) {
            if (category.getId().equals(id)) {
                return category;
            }
        }
        return null;
    }

    /**
     * Vérifie si un ID de catégorie est valide.
     * @param id L'identifiant à vérifier
     * @return true si la catégorie existe, false sinon
     */
    public static boolean isValid(String id) {
        return getById(id) != null;
    }
}
