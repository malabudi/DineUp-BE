package com.malabudi.dineupbe.order.mapper;

import com.malabudi.dineupbe.order.dto.OrderDto;
import com.malabudi.dineupbe.order.model.Order;

public class OrderMapper {
    public static OrderDto toOrderDto(Order order) {
        return new OrderDto(
                order.getId(),
                order.getCustomer().getId(),
                order.getCustomer().getFirstName() +
                        " " +
                        order.getCustomer().getLastName(),
                order.getLineItems().stream()
                        .map(LineItemMapper::toLineItemDto)
                        .toList(),
                order.getOrderDate(),
                order.getOrderStatus(),
                order.getTotal()
        );
    }
}
