package com.omp.port.entity;

import com.omp.common.entity.Asset;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/**
 * Extension 1:1 de Asset (asset_type = VESSEL) - un navire est un actif physique persistant
 * identifie par son IMO, reutilise sur plusieurs escales (cf plan section 1.3), pas une table
 * maitre separee.
 */
@Entity
@Table(name = "vessel_detail")
public class VesselDetail {

    @Id
    private Long assetId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @Column(name = "imo_number", unique = true, length = 20)
    private String imoNumber;

    @Column(length = 60)
    private String flag;

    @Column(name = "vessel_type", length = 60)
    private String vesselType;

    @Column(name = "length_m", precision = 6, scale = 2)
    private BigDecimal lengthM;

    @Column(name = "gross_tonnage", precision = 10, scale = 2)
    private BigDecimal grossTonnage;

    public Long getAssetId() {
        return assetId;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public String getImoNumber() {
        return imoNumber;
    }

    public void setImoNumber(String imoNumber) {
        this.imoNumber = imoNumber;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getVesselType() {
        return vesselType;
    }

    public void setVesselType(String vesselType) {
        this.vesselType = vesselType;
    }

    public BigDecimal getLengthM() {
        return lengthM;
    }

    public void setLengthM(BigDecimal lengthM) {
        this.lengthM = lengthM;
    }

    public BigDecimal getGrossTonnage() {
        return grossTonnage;
    }

    public void setGrossTonnage(BigDecimal grossTonnage) {
        this.grossTonnage = grossTonnage;
    }
}
