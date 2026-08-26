package com.omp.common.repository;

import com.omp.common.entity.OperationStep;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class OperationStepRepository extends GenericRepository<OperationStep, Long> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<OperationStep> entityClass() {
        return OperationStep.class;
    }

    public List<OperationStep> findByOperationId(Long operationId) {
        return em.createQuery(
                        "SELECT s FROM OperationStep s WHERE s.operation.id = :operationId ORDER BY s.sequence",
                        OperationStep.class)
                .setParameter("operationId", operationId)
                .getResultList();
    }
}
