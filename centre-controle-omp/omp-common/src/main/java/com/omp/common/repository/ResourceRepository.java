package com.omp.common.repository;

import com.omp.common.entity.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;

@ApplicationScoped
public class ResourceRepository extends GenericRepository<Resource, Long> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<Resource> entityClass() {
        return Resource.class;
    }

    public Optional<Resource> findByCode(String code) {
        try {
            return Optional.of(em.createQuery("SELECT r FROM Resource r WHERE r.code = :code", Resource.class)
                    .setParameter("code", code)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
