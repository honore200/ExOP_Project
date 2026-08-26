package com.omp.port.repository;

import com.omp.common.repository.GenericRepository;
import com.omp.port.entity.PortCall;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PortCallRepository extends GenericRepository<PortCall, Long> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<PortCall> entityClass() {
        return PortCall.class;
    }

    public Optional<PortCall> findByOperationId(Long operationId) {
        try {
            return Optional.of(em.createQuery(
                            "SELECT p FROM PortCall p WHERE p.operation.id = :operationId", PortCall.class)
                    .setParameter("operationId", operationId)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<PortCall> findRecent(int limit) {
        return em.createQuery(
                        "SELECT p FROM PortCall p ORDER BY p.operation.startDatetime DESC", PortCall.class)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<PortCall> findAtBerth() {
        return em.createQuery(
                        "SELECT p FROM PortCall p WHERE p.atdDatetime IS NULL AND p.ataDatetime IS NOT NULL",
                        PortCall.class)
                .getResultList();
    }
}
