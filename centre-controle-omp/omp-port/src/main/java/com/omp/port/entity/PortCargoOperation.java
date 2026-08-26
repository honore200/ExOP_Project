package com.omp.port.entity;

import com.omp.common.entity.OperationStep;
import com.omp.port.enums.CargoDirection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Reprend le role de port_manutention (doc1) : 1:N par escale, une escale peut charger ET
 * decharger plusieurs types de marchandise. */
@Entity
@Table(name = "port_cargo_operation")
public class PortCargoOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "port_call_id", nullable = false)
    private PortCall portCall;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_step_id")
    private OperationStep operationStep;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cargo_type_id", nullable = false)
    private CargoType cargoType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CargoDirection direction;

    @Column(precision = 12, scale = 2)
    private BigDecimal tonnage;

    @Column(name = "container_count")
    private Integer containerCount;

    @Column(name = "start_datetime")
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime")
    private LocalDateTime endDatetime;

    public Long getId() {
        return id;
    }

    public PortCall getPortCall() {
        return portCall;
    }

    public void setPortCall(PortCall portCall) {
        this.portCall = portCall;
    }

    public OperationStep getOperationStep() {
        return operationStep;
    }

    public void setOperationStep(OperationStep operationStep) {
        this.operationStep = operationStep;
    }

    public CargoType getCargoType() {
        return cargoType;
    }

    public void setCargoType(CargoType cargoType) {
        this.cargoType = cargoType;
    }

    public CargoDirection getDirection() {
        return direction;
    }

    public void setDirection(CargoDirection direction) {
        this.direction = direction;
    }

    public BigDecimal getTonnage() {
        return tonnage;
    }

    public void setTonnage(BigDecimal tonnage) {
        this.tonnage = tonnage;
    }

    public Integer getContainerCount() {
        return containerCount;
    }

    public void setContainerCount(Integer containerCount) {
        this.containerCount = containerCount;
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
