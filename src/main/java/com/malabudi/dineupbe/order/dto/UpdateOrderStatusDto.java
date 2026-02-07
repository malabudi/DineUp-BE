package com.malabudi.dineupbe.order.dto;

import com.malabudi.dineupbe.common.util.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusDto(
        @NotNull(message = "Order ID is required")
        Long orderId,

        @NotNull(message = "Order status is required")
        OrderStatus orderStatus
) {
}
