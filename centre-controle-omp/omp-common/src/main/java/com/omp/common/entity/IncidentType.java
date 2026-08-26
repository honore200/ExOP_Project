package com.omp.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "incident_type", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class IncidentType extends ReferenceEntity {

    @Column(length = 40)
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
