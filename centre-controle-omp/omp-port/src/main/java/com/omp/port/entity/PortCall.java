package com.omp.port.entity;

import com.omp.common.entity.Asset;
import com.omp.common.entity.Location;
import com.omp.common.entity.Operation;
import com.omp.common.entity.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Specialisation Port d'une Operation (1:0..1), proposee par analogie avec RailwayRotation
 * (cf plan section 1.3 / docs/proposition-port-call.md - a valider avec les equipes portuaires).
 * Le "motif d'escale" n'est pas un champ dedie : il est porte par operation.operationType
 * (SHIP_CALL_LOADING/UNLOADING/BUNKERING/MAINTENANCE), reutilisant le referentiel generique.
 */
@Entity
@Table(name = "port_call")
public class PortCall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "operation_id", nullable = false, unique = true)
    private Operation operation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vessel_asset_id", nullable = false)
    private Asset vessel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quay_location_id")
    private Location quay;

    @Column(name = "eta_datetime")
    private LocalDateTime etaDatetime;

    @Column(name = "ata_datetime")
    private LocalDateTime ataDatetime;

    @Column(name = "etd_datetime")
    private LocalDateTime etdDatetime;

    @Column(name = "atd_datetime")
    private LocalDateTime atdDatetime;

    @Column(name = "declared_tonnage", precision = 12, scale = 2)
    private BigDecimal declaredTonnage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;

    public Long getId() {
        return id;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Asset getVessel() {
        return vessel;
    }

    public void setVessel(Asset vessel) {
        this.vessel = vessel;
    }

    public Location getQuay() {
        return quay;
    }

    public void setQuay(Location quay) {
        this.quay = quay;
    }

    public LocalDateTime getEtaDatetime() {
        return etaDatetime;
    }

    public void setEtaDatetime(LocalDateTime etaDatetime) {
        this.etaDatetime = etaDatetime;
    }

    public LocalDateTime getAtaDatetime() {
        return ataDatetime;
    }

    public void setAtaDatetime(LocalDateTime ataDatetime) {
        this.ataDatetime = ataDatetime;
    }

    public LocalDateTime getEtdDatetime() {
        return etdDatetime;
    }

    public void setEtdDatetime(LocalDateTime etdDatetime) {
        this.etdDatetime = etdDatetime;
    }

    public LocalDateTime getAtdDatetime() {
        return atdDatetime;
    }

    public void setAtdDatetime(LocalDateTime atdDatetime) {
        this.atdDatetime = atdDatetime;
    }

    public BigDecimal getDeclaredTonnage() {
        return declaredTonnage;
    }

    public void setDeclaredTonnage(BigDecimal declaredTonnage) {
        this.declaredTonnage = declaredTonnage;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
