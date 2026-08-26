package com.omp.port.repository;

import com.omp.common.repository.ReferenceRepository;
import com.omp.port.entity.CargoType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class CargoTypeRepository extends ReferenceRepository<CargoType> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<CargoType> entityClass() {
        return CargoType.class;
    }
}
