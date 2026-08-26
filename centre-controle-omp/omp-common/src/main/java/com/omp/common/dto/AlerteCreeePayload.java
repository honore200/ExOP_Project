package com.omp.common.dto;

import com.omp.common.enums.Severity;

public record AlerteCreeePayload(
        Long alertId,
        Long eventId,
        String alertType,
        Severity severity
) {
}
