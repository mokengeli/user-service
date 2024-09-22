package com.bacos.mokengeli.biloko.application.model;

public enum PermissionEnum {
    CREATE_ORDER("Permission de créer des commandes"),
    VIEW_INVENTORY("Permission de visualiser les stocks"),
    EDIT_INVENTORY("Permission de modifier les stocks"),
    DELETE_ORDER("Permission de supprimer des commandes"),
    VIEW_REPORTS("Permission de visualiser les rapports");

    private final String description;

    PermissionEnum(String description) {
        this.description = description;
    }

    public String getLabel() {
        return description;
    }
}