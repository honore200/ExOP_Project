package com.omp.common.repository;

import com.omp.common.entity.ClientDetail;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class ClientDetailRepository extends GenericRepository<ClientDetail, Long> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<ClientDetail> entityClass() {
        return ClientDetail.class;
    }

    public List<ClientDetail> findByClientId(Long clientId) {
        return em.createQuery(
                        "SELECT d FROM ClientDetail d WHERE d.client.id = :clientId", ClientDetail.class)
                .setParameter("clientId", clientId)
                .getResultList();
    }
}
