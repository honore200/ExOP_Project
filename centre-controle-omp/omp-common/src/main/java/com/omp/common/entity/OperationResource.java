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
@Table(name = "operation_resource")
public class OperationResource {

    @EmbeddedId
    private OperationResourceId id = new OperationResourceId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("operationId")
    @JoinColumn(name = "operation_id", nullable = false)
    private Operation operation;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("resourceId")
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @Column(length = 40)
    private String role;

    @Column(name = "start_datetime")
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime")
    private LocalDateTime endDatetime;

    public OperationResource() {
    }

    public OperationResource(Operation operation, Resource resource, String role) {
        this.operation = operation;
        this.resource = resource;
        this.role = role;
        this.id = new OperationResourceId(operation.getId(), resource.getId());
    }

    public OperationResourceId getId() {
        return id;
    }

    public Operation getOperation() {
        return operation;
    }

    public Resource getResource() {
        return resource;
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
