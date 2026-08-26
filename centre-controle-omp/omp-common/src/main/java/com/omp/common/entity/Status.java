package com.omp.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Referentiel de statuts partage, discrimine par categorie (OPERATION, RAILWAY_ROTATION,
 * PORT_CALL, ASSET, RESOURCE...). Comble un trou du MCD doc2 : plusieurs entites y referencent
 * un status_id sans qu'aucune table "status" ne soit definie (cf plan section 1.4 point 1).
 */
@Entity
@Table(name = "status", uniqueConstraints = @UniqueConstraint(columnNames = {"category", "code"}))
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String category;

    @Column(nullable = false, length = 40)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
}
