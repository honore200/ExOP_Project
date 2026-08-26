package com.omp.common.repository;

import com.omp.common.entity.Asset;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AssetRepository extends GenericRepository<Asset, Long> {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return em;
    }

    @Override
    protected Class<Asset> entityClass() {
        return Asset.class;
    }

    public Optional<Asset> findByAssetCode(String assetCode) {
        try {
            return Optional.of(em.createQuery(
                            "SELECT a FROM Asset a WHERE a.assetCode = :assetCode", Asset.class)
                    .setParameter("assetCode", assetCode)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Asset> findByTypeCode(String assetTypeCode) {
        return em.createQuery(
                        "SELECT a FROM Asset a WHERE a.assetType.code = :code", Asset.class)
                .setParameter("code", assetTypeCode)
                .getResultList();
    }
}
