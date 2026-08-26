package com.omp.common.repository;

import com.omp.common.entity.Role;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;

@ApplicationScoped
public class RoleRepository extends GenericRepository<Role, Long> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<Role> entityClass() {
        return Role.class;
    }

    public Optional<Role> findByCode(String code) {
        try {
            return Optional.of(em.createQuery(
                            "SELECT r FROM Role r WHERE r.code = :code", Role.class)
                    .setParameter("code", code)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
