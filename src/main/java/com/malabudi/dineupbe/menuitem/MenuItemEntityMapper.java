package com.malabudi.dineupbe.menuitem;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class MenuItemEntityMapper implements Function<MenuItemDto, MenuItem> {

    @Override
    public MenuItem apply(MenuItemDto menuItemDto) {
        return new MenuItem(
                menuItemDto.name(),
                menuItemDto.description(),
                menuItemDto.price(),
                menuItemDto.imageUrl()
        );
    }
}
