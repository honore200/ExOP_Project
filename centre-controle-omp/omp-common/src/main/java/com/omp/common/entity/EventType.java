package com.omp.common.entity;

import com.omp.common.enums.Severity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "event_type", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class EventType extends ReferenceEntity {

    @Column(length = 40)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_severity", nullable = false, length = 20)
    private Severity defaultSeverity = Severity.INFO;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Severity getDefaultSeverity() {
        return defaultSeverity;
    }

    public void setDefaultSeverity(Severity defaultSeverity) {
        this.defaultSeverity = defaultSeverity;
    }
}
