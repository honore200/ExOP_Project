package com.omp.common.repository;

import com.omp.common.entity.Domain;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;

@ApplicationScoped
public class DomainRepository extends GenericRepository<Domain, Long> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<Domain> entityClass() {
        return Domain.class;
    }

    public Optional<Domain> findByCode(String code) {
        try {
            return Optional.of(em.createQuery(
                            "SELECT d FROM Domain d WHERE d.code = :code", Domain.class)
                    .setParameter("code", code)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
