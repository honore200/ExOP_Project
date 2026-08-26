package com.omp.common.repository;

import com.omp.common.entity.OperationType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class OperationTypeRepository extends GenericRepository<OperationType, Long> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<OperationType> entityClass() {
        return OperationType.class;
    }

    public List<OperationType> findByDomainCode(String domainCode) {
        return em.createQuery(
                        "SELECT t FROM OperationType t WHERE t.domain.code = :domainCode AND t.active = true",
                        OperationType.class)
                .setParameter("domainCode", domainCode)
                .getResultList();
    }

    public Optional<OperationType> findByDomainCodeAndCode(String domainCode, String code) {
        try {
            return Optional.of(em.createQuery(
                            "SELECT t FROM OperationType t WHERE t.domain.code = :domainCode AND t.code = :code",
                            OperationType.class)
                    .setParameter("domainCode", domainCode)
                    .setParameter("code", code)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
