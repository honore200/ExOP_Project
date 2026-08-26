package com.omp.web.bean;

import com.omp.common.entity.Asset;
import com.omp.common.entity.Operation;
import com.omp.common.service.AssetService;
import com.omp.common.service.OperationService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

/** Construit sur Asset + Operation(domain=MAINTENANCE), pas de nouvelle table (cf plan Phase 5). */
@Named
@ViewScoped
public class MaintenanceBean implements Serializable {

    @Inject
    private AssetService assetService;

    @Inject
    private OperationService operationService;

    private List<Asset> assets;
    private List<Operation> maintenanceOperations;

    public void init() {
        assets = assetService.findAll();
        maintenanceOperations = operationService.findByDomainCode("MAINTENANCE");
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public List<Operation> getMaintenanceOperations() {
        return maintenanceOperations;
    }

    public long getAssetsUnderMaintenance() {
        return assets == null ? 0 : assets.stream()
                .filter(a -> a.getStatus() != null && "UNDER_MAINTENANCE".equals(a.getStatus().getCode()))
                .count();
    }

    public long getAssetsOutOfService() {
        return assets == null ? 0 : assets.stream()
                .filter(a -> a.getStatus() != null && "OUT_OF_SERVICE".equals(a.getStatus().getCode()))
                .count();
    }
}
