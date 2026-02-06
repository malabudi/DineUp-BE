package com.malabudi.dineupbe.menu.mapper;

import com.malabudi.dineupbe.menu.dto.MenuItemDto;
import com.malabudi.dineupbe.menu.model.MenuGroup;
import com.malabudi.dineupbe.menu.model.MenuItem;
import org.springframework.stereotype.Component;

@Component
public class MenuItemMapper {
    public static MenuItemDto toDto(MenuItem menuItem) {
        return new MenuItemDto(
                menuItem.getId(),
                menuItem.getMenuGroup().getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getImageUrl()
        );
    }
}
