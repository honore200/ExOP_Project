package com.omp.common.repository;

import com.omp.common.entity.Location;
import com.omp.common.enums.LocationType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class LocationRepository extends GenericRepository<Location, Long> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<Location> entityClass() {
        return Location.class;
    }

    public List<Location> findByType(LocationType type) {
        return em.createQuery(
                        "SELECT l FROM Location l WHERE l.locationType = :type", Location.class)
                .setParameter("type", type)
                .getResultList();
    }
}
