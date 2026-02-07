package com.malabudi.dineupbe.order.mapper;

import com.malabudi.dineupbe.order.dto.ResponseLineItemDto;
import com.malabudi.dineupbe.order.model.LineItem;

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
