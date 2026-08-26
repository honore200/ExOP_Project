package com.omp.common.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OperationAssetId implements Serializable {

    private Long operationId;
    private Long assetId;

    public OperationAssetId() {
    }

    public OperationAssetId(Long operationId, Long assetId) {
        this.operationId = operationId;
        this.assetId = assetId;
    }

    public Long getOperationId() {
        return operationId;
    }

    public Long getAssetId() {
        return assetId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OperationAssetId)) return false;
        OperationAssetId that = (OperationAssetId) o;
        return Objects.equals(operationId, that.operationId) && Objects.equals(assetId, that.assetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationId, assetId);
    }
}
