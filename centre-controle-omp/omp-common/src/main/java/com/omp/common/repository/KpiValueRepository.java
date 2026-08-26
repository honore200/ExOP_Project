package com.omp.common.repository;

import com.omp.common.entity.KpiValue;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class KpiValueRepository extends GenericRepository<KpiValue, Long> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<KpiValue> entityClass() {
        return KpiValue.class;
    }

    public List<KpiValue> findByOperationId(Long operationId) {
        return em.createQuery(
                        "SELECT v FROM KpiValue v WHERE v.operation.id = :operationId ORDER BY v.calculatedAt DESC",
                        KpiValue.class)
                .setParameter("operationId", operationId)
                .getResultList();
    }

    public List<KpiValue> findByKpiCode(String kpiCode, int limit) {
        return em.createQuery(
                        "SELECT v FROM KpiValue v WHERE v.kpi.code = :code ORDER BY v.calculatedAt DESC",
                        KpiValue.class)
                .setParameter("code", kpiCode)
                .setMaxResults(limit)
                .getResultList();
    }

    public boolean existsForOperationAndKpi(Long operationId, String kpiCode) {
        Long count = em.createQuery(
                        "SELECT COUNT(v) FROM KpiValue v WHERE v.operation.id = :operationId AND v.kpi.code = :code",
                        Long.class)
                .setParameter("operationId", operationId)
                .setParameter("code", kpiCode)
                .getSingleResult();
        return count > 0;
    }
}
