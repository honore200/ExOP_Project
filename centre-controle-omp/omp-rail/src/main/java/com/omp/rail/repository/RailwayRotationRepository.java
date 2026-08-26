package com.omp.rail.repository;

import com.omp.common.repository.GenericRepository;
import com.omp.rail.entity.RailwayRotation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RailwayRotationRepository extends GenericRepository<RailwayRotation, Long> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<RailwayRotation> entityClass() {
        return RailwayRotation.class;
    }

    public Optional<RailwayRotation> findByOperationId(Long operationId) {
        try {
            return Optional.of(em.createQuery(
                            "SELECT r FROM RailwayRotation r WHERE r.operation.id = :operationId", RailwayRotation.class)
                    .setParameter("operationId", operationId)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<RailwayRotation> findRecent(int limit) {
        return em.createQuery(
                        "SELECT r FROM RailwayRotation r ORDER BY r.operation.startDatetime DESC", RailwayRotation.class)
                .setMaxResults(limit)
                .getResultList();
    }
}
