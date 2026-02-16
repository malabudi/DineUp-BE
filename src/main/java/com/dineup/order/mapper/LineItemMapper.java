package com.dineup.order.mapper;

import com.dineup.order.dto.ResponseLineItemDto;
import com.dineup.order.model.LineItem;

public class LineItemMapper {
    public static ResponseLineItemDto toDto(LineItem lineItem) {
        return new ResponseLineItemDto(
                lineItem.getId(),
                lineItem.getOrder().getId(),
                lineItem.getMenuItem().getId(),
                lineItem.getMenuItem().getName(),
                lineItem.getQuantity(),
                lineItem.getPrice(),
                lineItem.getLineTotal()
        );
    }
}
