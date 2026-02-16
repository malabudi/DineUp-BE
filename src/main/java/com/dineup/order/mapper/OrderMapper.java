package com.dineup.order.mapper;

import com.dineup.order.dto.ResponseOrderDto;
import com.dineup.order.model.Order;

public class OrderMapper {
    public static ResponseOrderDto toDto(Order order) {
        return new ResponseOrderDto(
                order.getId(),
                order.getCustomer().getId(),
                order.getCustomer().getFirstName() +
                        " " +
                        order.getCustomer().getLastName(),
                order.getLineItems().stream()
                        .map(LineItemMapper::toDto)
                        .toList(),
                order.getOrderDate(),
                order.getOrderStatus(),
                order.getTotal()
        );
    }
}
