package com.omp.common.repository;

import com.omp.common.entity.AuditLog;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class AuditLogRepository extends GenericRepository<AuditLog, Long> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<AuditLog> entityClass() {
        return AuditLog.class;
    }

    public List<AuditLog> findByEntityTypeAndId(String entityType, Long entityId) {
        return em.createQuery(
                        "SELECT l FROM AuditLog l WHERE l.entityType = :type AND l.entityId = :id ORDER BY l.createdAt DESC",
                        AuditLog.class)
                .setParameter("type", entityType)
                .setParameter("id", entityId)
                .getResultList();
    }

    public List<AuditLog> findRecent(int limit) {
        return em.createQuery("SELECT l FROM AuditLog l ORDER BY l.createdAt DESC", AuditLog.class)
                .setMaxResults(limit)
                .getResultList();
    }
}
