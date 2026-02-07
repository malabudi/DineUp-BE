package com.malabudi.dineupbe.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RequestLineItemDto(
        @NotNull(message = "Menu item id is required")
        Long menuItemId,

        @Positive(message = "A quantity greater than 0 is required")
        @NotNull(message = "Item quantity is required")
        Integer quantity
) {}
