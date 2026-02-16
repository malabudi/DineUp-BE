package com.dineup.menu.mapper;

import com.dineup.menu.dto.ResponseMenuItemDto;
import com.dineup.menu.model.MenuItem;
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
