package com.omp.common.repository;

import com.omp.common.entity.Status;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class StatusRepository extends GenericRepository<Status, Long> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<Status> entityClass() {
        return Status.class;
    }

    public List<Status> findByCategory(String category) {
        return em.createQuery(
                        "SELECT s FROM Status s WHERE s.category = :category", Status.class)
                .setParameter("category", category)
                .getResultList();
    }

    public Optional<Status> findByCategoryAndCode(String category, String code) {
        try {
            return Optional.of(em.createQuery(
                            "SELECT s FROM Status s WHERE s.category = :category AND s.code = :code",
                            Status.class)
                    .setParameter("category", category)
                    .setParameter("code", code)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
