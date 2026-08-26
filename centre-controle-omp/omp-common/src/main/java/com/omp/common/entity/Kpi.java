package com.omp.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "kpi", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class Kpi extends ReferenceEntity {

    @Column(length = 20)
    private String unit;

    @Column(name = "calculation_type", length = 40)
    private String calculationType;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCalculationType() {
        return calculationType;
    }

    public void setCalculationType(String calculationType) {
        this.calculationType = calculationType;
    }
}
