package com.omp.common.repository;

import com.omp.common.entity.EventType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class EventTypeRepository extends ReferenceRepository<EventType> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<EventType> entityClass() {
        return EventType.class;
    }
}
