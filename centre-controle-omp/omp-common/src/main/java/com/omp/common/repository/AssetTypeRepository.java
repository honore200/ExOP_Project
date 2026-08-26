package com.omp.common.repository;

import com.omp.common.entity.AssetType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class AssetTypeRepository extends ReferenceRepository<AssetType> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<AssetType> entityClass() {
        return AssetType.class;
    }
}
