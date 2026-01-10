package com.malabudi.dineupbe.menuitem;

import org.springframework.stereotype.Service;
import java.util.function.Function;

@Service
public class MenuItemDTOMapper implements Function<MenuItem, MenuItemDto> {
    @Override
    public MenuItemDto apply(MenuItem menuItem) {
        return new MenuItemDto(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getImageUrl()
        );
    }
}
