package com.omp.common.repository;

import com.omp.common.entity.IncidentType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class IncidentTypeRepository extends ReferenceRepository<IncidentType> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<IncidentType> entityClass() {
        return IncidentType.class;
    }
}
