package com.omp.common.service;

import com.omp.common.entity.Asset;
import com.omp.common.interceptor.Audited;
import com.omp.common.repository.AssetRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;

@Stateless
public class AssetService {

    @Inject
    private AssetRepository assetRepository;

    public List<Asset> findAll() {
        return assetRepository.findAll();
    }

    public List<Asset> findByTypeCode(String assetTypeCode) {
        return assetRepository.findByTypeCode(assetTypeCode);
    }

    @Audited(action = "CREATE", entityType = "Asset")
    public Asset create(Asset asset) {
        return assetRepository.save(asset);
    }

    @Audited(action = "UPDATE", entityType = "Asset")
    public Asset update(Asset asset) {
        return assetRepository.update(asset);
    }
}
