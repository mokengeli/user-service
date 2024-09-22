package com.bacos.mokengeli.biloko.application.model;

public enum RoleEnum {
    ROLE_ADMIN("Administrateur du système"),
    ROLE_USER("Utilisateur standard"),
    ROLE_MANAGER("Responsable du lounge/restaurant"),
    ROLE_SERVER("Serveur dans le lounge/restaurant");

    private final String description;

    RoleEnum(String description) {
        this.description = description;
    }

    public String getLabel() {
        return description;
    }
}