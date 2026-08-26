package com.omp.common.repository;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * CRUD de base partage par les ~20 entites du modele (evite de dupliquer find/persist/merge/remove
 * dans chaque repository concret). Chaque sous-classe fournit son EntityManager via @PersistenceContext
 * et precise sa classe d'entite.
 */
public abstract class GenericRepository<T, ID> {

    protected abstract EntityManager entityManager();

    protected abstract Class<T> entityClass();

    public T save(T entity) {
        entityManager().persist(entity);
        return entity;
    }

    public T update(T entity) {
        return entityManager().merge(entity);
    }

    public Optional<T> findById(ID id) {
        return Optional.ofNullable(entityManager().find(entityClass(), id));
    }

    public List<T> findAll() {
        return entityManager()
                .createQuery("SELECT e FROM " + entityClass().getSimpleName() + " e", entityClass())
                .getResultList();
    }

    public void delete(T entity) {
        entityManager().remove(entityManager().contains(entity) ? entity : entityManager().merge(entity));
    }

    public void deleteById(ID id) {
        findById(id).ifPresent(this::delete);
    }
}
