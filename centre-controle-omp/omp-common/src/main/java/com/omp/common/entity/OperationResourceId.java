package com.omp.common.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OperationResourceId implements Serializable {

    private Long operationId;
    private Long resourceId;

    public OperationResourceId() {
    }

    public OperationResourceId(Long operationId, Long resourceId) {
        this.operationId = operationId;
        this.resourceId = resourceId;
    }

    public Long getOperationId() {
        return operationId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OperationResourceId)) return false;
        OperationResourceId that = (OperationResourceId) o;
        return Objects.equals(operationId, that.operationId) && Objects.equals(resourceId, that.resourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationId, resourceId);
    }
}
