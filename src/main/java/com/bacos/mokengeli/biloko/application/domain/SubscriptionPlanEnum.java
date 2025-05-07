package com.bacos.mokengeli.biloko.application.domain;

import lombok.Getter;

@Getter
public enum SubscriptionPlanEnum {
    STARTER("Starter"),
    PREMIUM("Premium");

    private final String label;

    SubscriptionPlanEnum(String label) {
        this.label = label;
    }

}