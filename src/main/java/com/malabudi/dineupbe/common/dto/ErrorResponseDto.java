package com.malabudi.dineupbe.common.dto;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        LocalDateTime timestamp,
        int status,
        String errorName,
        String errorMessage,
        String path
) {}
