package com.bacos.mokengeli.biloko.application.domain;

/**
 * Énumération des statuts possibles pour les demandes d'activation d'addons.
 * Workflow: PENDING → APPROVED/REJECTED
 */
public enum AddonRequestStatusEnum {
    PENDING("PENDING", "En attente", "Demande en attente de traitement par un administrateur"),
    APPROVED("APPROVED", "Approuvée", "Demande approuvée et feature activée"),
    REJECTED("REJECTED", "Rejetée", "Demande rejetée par un administrateur");

    private final String code;
    private final String label;
    private final String description;

    AddonRequestStatusEnum(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Recherche un statut par son code.
     * @param code Le code du statut (ex: "PENDING")
     * @return L'enum correspondant ou null si non trouvé
     */
    public static AddonRequestStatusEnum getByCode(String code) {
        if (code == null) return null;
        for (AddonRequestStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * Vérifie si un code de statut est valide.
     * @param code Le code à vérifier
     * @return true si le statut existe, false sinon
     */
    public static boolean isValid(String code) {
        return getByCode(code) != null;
    }
}
