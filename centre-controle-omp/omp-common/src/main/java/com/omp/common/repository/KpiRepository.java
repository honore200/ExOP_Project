package com.omp.common.repository;

import com.omp.common.entity.Kpi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class KpiRepository extends ReferenceRepository<Kpi> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<Kpi> entityClass() {
        return Kpi.class;
    }
}
