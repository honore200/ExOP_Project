package com.omp.common.repository;

import com.omp.common.entity.Incident;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class IncidentRepository extends GenericRepository<Incident, Long> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<Incident> entityClass() {
        return Incident.class;
    }

    public List<Incident> findOpen() {
        return em.createQuery(
                        "SELECT i FROM Incident i WHERE i.resolved = false ORDER BY i.severity DESC, i.startDatetime DESC",
                        Incident.class)
                .getResultList();
    }

    public List<Incident> findByOperationId(Long operationId) {
        return em.createQuery(
                        "SELECT i FROM Incident i WHERE i.operation.id = :operationId ORDER BY i.startDatetime DESC",
                        Incident.class)
                .setParameter("operationId", operationId)
                .getResultList();
    }
}
