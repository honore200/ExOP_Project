package com.omp.common.dto;

import java.util.List;

public record UserDTO(
        Long id,
        String username,
        String fullName,
        boolean enabled,
        List<String> roles
) {
}
