package com.omp.common.dto;

import java.time.LocalDateTime;

public record OperationDTO(
        Long id,
        String operationCode,
        String operationTypeCode,
        String domainCode,
        String clientCode,
        String statusCode,
        String locationCode,
        LocalDateTime startDatetime,
        LocalDateTime endDatetime,
        String description
) {
}
