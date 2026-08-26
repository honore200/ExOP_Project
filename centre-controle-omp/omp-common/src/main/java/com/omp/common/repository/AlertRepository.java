package com.omp.common.repository;

import com.omp.common.entity.Alert;
import com.omp.common.enums.AlertStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class AlertRepository extends GenericRepository<Alert, Long> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<Alert> entityClass() {
        return Alert.class;
    }

    public List<Alert> findByStatus(AlertStatus status) {
        return em.createQuery(
                        "SELECT a FROM Alert a WHERE a.status = :status ORDER BY a.severity DESC, a.createdAt DESC",
                        Alert.class)
                .setParameter("status", status)
                .getResultList();
    }
}
