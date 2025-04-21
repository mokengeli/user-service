package com.bacos.mokengeli.biloko.infrastructure.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tenant_user_sequence")
public class TenantUserSequence {

    @Id
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "last_value", nullable = false)
    private Long lastValue;

    protected TenantUserSequence() { }

    public TenantUserSequence(Long tenantId, Long lastValue) {
        this.tenantId = tenantId;
        this.lastValue = lastValue;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Long getLastValue() {
        return lastValue;
    }

    public void setLastValue(Long lastValue) {
        this.lastValue = lastValue;
    }
}
