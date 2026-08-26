package com.omp.common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * doc2 decrit step_type(id, code, name, domain) avec "domain" en simple attribut ; modelise ici en
 * FK vers Domain pour rester coherent avec le reste du referentiel (cf plan section 1 - toute
 * donnee de type/categorie passe par une table de reference, jamais par une colonne libre).
 */
@Entity
@Table(name = "step_type", uniqueConstraints = @UniqueConstraint(columnNames = {"domain_id", "code"}))
public class StepType extends ReferenceEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "domain_id", nullable = false)
    private Domain domain;

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }
}
