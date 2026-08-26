package com.omp.common.repository;

import com.omp.common.entity.OperationResource;
import com.omp.common.entity.OperationResourceId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class OperationResourceRepository extends GenericRepository<OperationResource, OperationResourceId> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<OperationResource> entityClass() {
        return OperationResource.class;
    }

    public List<OperationResource> findByOperationId(Long operationId) {
        return em.createQuery(
                        "SELECT r FROM OperationResource r WHERE r.operation.id = :operationId", OperationResource.class)
                .setParameter("operationId", operationId)
                .getResultList();
    }
}
