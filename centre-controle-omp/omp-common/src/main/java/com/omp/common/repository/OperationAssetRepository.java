package com.omp.common.repository;

import com.omp.common.entity.OperationAsset;
import com.omp.common.entity.OperationAssetId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class OperationAssetRepository extends GenericRepository<OperationAsset, OperationAssetId> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<OperationAsset> entityClass() {
        return OperationAsset.class;
    }

    public List<OperationAsset> findByOperationId(Long operationId) {
        return em.createQuery(
                        "SELECT oa FROM OperationAsset oa WHERE oa.operation.id = :operationId", OperationAsset.class)
                .setParameter("operationId", operationId)
                .getResultList();
    }
}
