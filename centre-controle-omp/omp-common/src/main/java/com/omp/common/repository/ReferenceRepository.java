package com.omp.common.repository;

import com.omp.common.entity.ReferenceEntity;
import jakarta.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

/** Specialisation de GenericRepository pour les tables id/code/name/active (cf ReferenceEntity). */
public abstract class ReferenceRepository<T extends ReferenceEntity> extends GenericRepository<T, Long> {

    public Optional<T> findByCode(String code) {
        String jpql = "SELECT e FROM " + entityClass().getSimpleName() + " e WHERE e.code = :code";
        try {
            return Optional.of(entityManager().createQuery(jpql, entityClass())
                    .setParameter("code", code)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<T> findAllActive() {
        String jpql = "SELECT e FROM " + entityClass().getSimpleName() + " e WHERE e.active = true";
        return entityManager().createQuery(jpql, entityClass()).getResultList();
    }
}
