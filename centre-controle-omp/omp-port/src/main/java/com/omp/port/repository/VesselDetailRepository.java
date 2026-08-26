package com.omp.port.repository;

import com.omp.common.repository.GenericRepository;
import com.omp.port.entity.VesselDetail;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;

@ApplicationScoped
public class VesselDetailRepository extends GenericRepository<VesselDetail, Long> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<VesselDetail> entityClass() {
        return VesselDetail.class;
    }

    public Optional<VesselDetail> findByImoNumber(String imoNumber) {
        try {
            return Optional.of(em.createQuery(
                            "SELECT v FROM VesselDetail v WHERE v.imoNumber = :imo", VesselDetail.class)
                    .setParameter("imo", imoNumber)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
