package com.omp.common.repository;

import com.omp.common.entity.Client;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;

@ApplicationScoped
public class ClientRepository extends GenericRepository<Client, Long> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<Client> entityClass() {
        return Client.class;
    }

    public Optional<Client> findByCode(String code) {
        try {
            return Optional.of(em.createQuery("SELECT c FROM Client c WHERE c.code = :code", Client.class)
                    .setParameter("code", code)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
