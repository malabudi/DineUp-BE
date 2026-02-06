package com.malabudi.dineupbe.order.mapper;

import com.malabudi.dineupbe.order.dto.LineItemDto;
import com.malabudi.dineupbe.order.model.LineItem;

public class LineItemMapper {
    public static LineItemDto toDto(LineItem lineItem) {
        return new LineItemDto(
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
