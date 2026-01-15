package com.malabudi.dineupbe.mapper;

import com.malabudi.dineupbe.dto.MenuItemDto;
import com.malabudi.dineupbe.model.MenuItem;
import org.springframework.stereotype.Component;

@Component
public class MenuItemMapper {
    public MenuItemDto toDto(MenuItem menuItem) {
        return new MenuItemDto(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getImageUrl()
        );
    }

    public MenuItem toEntity(MenuItemDto menuItemDto) {
        return new MenuItem(
                menuItemDto.name(),
                menuItemDto.description(),
                menuItemDto.price(),
                menuItemDto.imageUrl()
        );
    }
}
