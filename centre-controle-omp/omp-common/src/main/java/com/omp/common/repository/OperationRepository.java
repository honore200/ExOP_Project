package com.omp.common.repository;

import com.omp.common.entity.Operation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class OperationRepository extends GenericRepository<Operation, Long> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<Operation> entityClass() {
        return Operation.class;
    }

    public Optional<Operation> findByOperationCode(String code) {
        try {
            return Optional.of(em.createQuery(
                            "SELECT o FROM Operation o WHERE o.operationCode = :code", Operation.class)
                    .setParameter("code", code)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /** Utilise par le dashboard general (cf omp-api DashboardAggregationService, Phase 5) */
    public List<Operation> findByDomainCode(String domainCode) {
        return em.createQuery(
                        "SELECT o FROM Operation o WHERE o.domain.code = :domainCode ORDER BY o.startDatetime DESC",
                        Operation.class)
                .setParameter("domainCode", domainCode)
                .getResultList();
    }

    public List<Operation> findActiveBetween(LocalDateTime from, LocalDateTime to) {
        return em.createQuery(
                        "SELECT o FROM Operation o WHERE o.startDatetime BETWEEN :from AND :to ORDER BY o.startDatetime",
                        Operation.class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    /** Utilise par PurgeTimerBean (omp-api/scheduler, Phase 6) - retention configurable via env var. */
    public List<Operation> findPurgeable(LocalDateTime cutoff) {
        return em.createQuery(
                        "SELECT o FROM Operation o WHERE o.endDatetime IS NOT NULL AND o.endDatetime < :cutoff "
                                + "AND o.status.code IN ('COMPLETED', 'CANCELLED')",
                        Operation.class)
                .setParameter("cutoff", cutoff)
                .getResultList();
    }
}
