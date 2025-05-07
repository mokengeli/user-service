package com.bacos.mokengeli.biloko.application.domain;

import lombok.Getter;

@Getter
public enum EstablishmentTypeEnum {
    RESTAURANT("Restaurant"),
    BAR("Bar"),
    LOUNGE("Lounge"),
    PLATFORM("Platform");

    private final String label;

    EstablishmentTypeEnum(String label) {
        this.label = label;
    }

}