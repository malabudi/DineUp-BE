package com.malabudi.dineupbe.menu.mapper;

import com.malabudi.dineupbe.menu.dto.RequestMenuItemDto;
import com.malabudi.dineupbe.menu.dto.ResponseMenuItemDto;
import com.malabudi.dineupbe.menu.model.MenuItem;
import org.springframework.stereotype.Component;

@Component
public class MenuItemMapper {
    public static ResponseMenuItemDto toDto(MenuItem menuItem) {
        return new ResponseMenuItemDto(
                menuItem.getId(),
                menuItem.getMenuGroup().getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getImageUrl()
        );
    }
}
