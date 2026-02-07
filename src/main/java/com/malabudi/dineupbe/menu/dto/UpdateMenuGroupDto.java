package com.malabudi.dineupbe.menu.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateMenuGroupDto(
        @NotBlank(message = "Menu group name is required")
        String name
) {
}
