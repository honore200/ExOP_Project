package com.omp.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "operation_asset")
public class OperationAsset {

    @EmbeddedId
    private OperationAssetId id = new OperationAssetId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("operationId")
    @JoinColumn(name = "operation_id", nullable = false)
    private Operation operation;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("assetId")
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(length = 40)
    private String role;

    @Column(name = "start_datetime")
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime")
    private LocalDateTime endDatetime;

    public OperationAsset() {
    }

    public OperationAsset(Operation operation, Asset asset, String role) {
        this.operation = operation;
        this.asset = asset;
        this.role = role;
        this.id = new OperationAssetId(operation.getId(), asset.getId());
    }

    public OperationAssetId getId() {
        return id;
    }

    public Operation getOperation() {
        return operation;
    }

    public Asset getAsset() {
        return asset;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(LocalDateTime startDatetime) {
        this.startDatetime = startDatetime;
    }

    public LocalDateTime getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(LocalDateTime endDatetime) {
        this.endDatetime = endDatetime;
    }
}
