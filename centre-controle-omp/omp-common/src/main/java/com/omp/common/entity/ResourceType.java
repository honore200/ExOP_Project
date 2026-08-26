package com.omp.common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "resource_type", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class ResourceType extends ReferenceEntity {
}
