package com.omp.common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "asset_type", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class AssetType extends ReferenceEntity {
}
