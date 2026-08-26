package com.omp.common.dto;

import java.time.Instant;
import java.util.List;

public record LoginResponse(
        String token,
        Instant expiresAt,
        UserDTO user,
        List<String> roles
) {
}
