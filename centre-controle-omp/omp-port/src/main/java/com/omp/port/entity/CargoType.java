package com.omp.port.entity;

import com.omp.common.entity.ReferenceEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "cargo_type", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class CargoType extends ReferenceEntity {
}
