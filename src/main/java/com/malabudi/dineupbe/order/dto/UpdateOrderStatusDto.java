package com.malabudi.dineupbe.order.dto;

import com.malabudi.dineupbe.common.util.OrderStatus;

public record UpdateOrderStatusDto(
        Long orderId,
        OrderStatus orderStatus
) {
}
