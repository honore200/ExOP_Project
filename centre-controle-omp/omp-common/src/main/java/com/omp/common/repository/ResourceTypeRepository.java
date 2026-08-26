package com.omp.common.repository;

import com.omp.common.entity.ResourceType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class ResourceTypeRepository extends ReferenceRepository<ResourceType> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<ResourceType> entityClass() {
        return ResourceType.class;
    }
}
