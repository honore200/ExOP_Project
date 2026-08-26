package com.omp.common.repository;

import com.omp.common.entity.OperationObservation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class OperationObservationRepository extends GenericRepository<OperationObservation, Long> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<OperationObservation> entityClass() {
        return OperationObservation.class;
    }

    public List<OperationObservation> findByOperationId(Long operationId) {
        return em.createQuery(
                        "SELECT o FROM OperationObservation o WHERE o.operation.id = :operationId ORDER BY o.createdAt DESC",
                        OperationObservation.class)
                .setParameter("operationId", operationId)
                .getResultList();
    }
}
