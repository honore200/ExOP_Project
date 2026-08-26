package com.omp.port.repository;

import com.omp.common.repository.GenericRepository;
import com.omp.port.entity.PortCargoOperation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class PortCargoOperationRepository extends GenericRepository<PortCargoOperation, Long> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<PortCargoOperation> entityClass() {
        return PortCargoOperation.class;
    }

    public List<PortCargoOperation> findByPortCallId(Long portCallId) {
        return em.createQuery(
                        "SELECT c FROM PortCargoOperation c WHERE c.portCall.id = :portCallId", PortCargoOperation.class)
                .setParameter("portCallId", portCallId)
                .getResultList();
    }
}
