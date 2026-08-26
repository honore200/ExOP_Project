package com.omp.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * Squelette commun aux ~6 tables de referentiel quasi-identiques (operation_type, step_type,
 * asset_type, resource_type, event_type, incident_type) : id/code/name/active. "active" permet de
 * retirer un code du choix futur sans jamais le supprimer (cf plan section 1.4 point 5) - les FK
 * vers ces tables restent ON DELETE RESTRICT.
 */
@MappedSuperclass
public abstract class ReferenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private boolean active = true;

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
