package com.malabudi.dineupbe.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RequestMenuItemDto(
        @NotNull(message = "Menu group ID is required")
        Long menuGroupId,

        @NotBlank(message = "Menu item name is required")
        String name,

        @NotBlank(message = "Menu item description is required")
        String description,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than zero")
        BigDecimal price,

        String imageUrl
) {}
