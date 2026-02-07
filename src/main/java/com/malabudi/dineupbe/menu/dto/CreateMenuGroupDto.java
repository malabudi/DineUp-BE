package com.malabudi.dineupbe.menu.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateMenuGroupDto(
        @NotBlank(message = "Menu group name is required")
        String name
) {
}
