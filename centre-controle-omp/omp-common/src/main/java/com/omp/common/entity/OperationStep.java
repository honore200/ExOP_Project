package com.omp.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.time.LocalDateTime;

@Entity
@Table(name = "operation_step", uniqueConstraints = @UniqueConstraint(columnNames = {"operation_id", "sequence"}))
public class OperationStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private long version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "operation_id", nullable = false)
    private Operation operation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "step_type_id", nullable = false)
    private StepType stepType;

    @Column(nullable = false)
    private int sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;

    @Column(name = "planned_start")
    private LocalDateTime plannedStart;

    @Column(name = "actual_start")
    private LocalDateTime actualStart;

    @Column(name = "actual_end")
    private LocalDateTime actualEnd;

    @Column(name = "duration_hours")
    private Double durationHours;

    @Column(name = "waiting_hours")
    private Double waitingHours;

    public Long getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public StepType getStepType() {
        return stepType;
    }

    public void setStepType(StepType stepType) {
        this.stepType = stepType;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getPlannedStart() {
        return plannedStart;
    }

    public void setPlannedStart(LocalDateTime plannedStart) {
        this.plannedStart = plannedStart;
    }

    public LocalDateTime getActualStart() {
        return actualStart;
    }

    public void setActualStart(LocalDateTime actualStart) {
        this.actualStart = actualStart;
    }

    public LocalDateTime getActualEnd() {
        return actualEnd;
    }

    public void setActualEnd(LocalDateTime actualEnd) {
        this.actualEnd = actualEnd;
    }

    public Double getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(Double durationHours) {
        this.durationHours = durationHours;
    }

    public Double getWaitingHours() {
        return waitingHours;
    }

    public void setWaitingHours(Double waitingHours) {
        this.waitingHours = waitingHours;
    }
}
