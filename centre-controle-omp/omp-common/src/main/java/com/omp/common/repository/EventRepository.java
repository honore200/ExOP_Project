package com.omp.common.repository;

import com.omp.common.entity.Event;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class EventRepository extends GenericRepository<Event, Long> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<Event> entityClass() {
        return Event.class;
    }

    public List<Event> findByOperationId(Long operationId) {
        return em.createQuery(
                        "SELECT e FROM Event e WHERE e.operation.id = :operationId ORDER BY e.eventDatetime DESC",
                        Event.class)
                .setParameter("operationId", operationId)
                .getResultList();
    }

    public List<Event> findRecent(int limit) {
        return em.createQuery("SELECT e FROM Event e ORDER BY e.eventDatetime DESC", Event.class)
                .setMaxResults(limit)
                .getResultList();
    }
}
