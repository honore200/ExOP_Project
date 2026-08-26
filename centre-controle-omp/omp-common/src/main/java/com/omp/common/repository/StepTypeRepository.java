package com.omp.common.repository;

import com.omp.common.entity.StepType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class StepTypeRepository extends GenericRepository<StepType, Long> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<StepType> entityClass() {
        return StepType.class;
    }

    public List<StepType> findByDomainCode(String domainCode) {
        return em.createQuery(
                        "SELECT t FROM StepType t WHERE t.domain.code = :domainCode AND t.active = true",
                        StepType.class)
                .setParameter("domainCode", domainCode)
                .getResultList();
    }

    public Optional<StepType> findByDomainCodeAndCode(String domainCode, String code) {
        try {
            return Optional.of(em.createQuery(
                            "SELECT t FROM StepType t WHERE t.domain.code = :domainCode AND t.code = :code",
                            StepType.class)
                    .setParameter("domainCode", domainCode)
                    .setParameter("code", code)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
