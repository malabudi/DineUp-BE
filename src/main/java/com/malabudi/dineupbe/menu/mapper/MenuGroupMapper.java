package com.malabudi.dineupbe.menu.mapper;

import com.malabudi.dineupbe.menu.dto.MenuGroupDto;
import com.malabudi.dineupbe.menu.model.MenuGroup;

import java.util.stream.Collectors;

public class MenuGroupMapper {
    public static MenuGroupDto toDto(MenuGroup menuGroup) {
        return new MenuGroupDto(
                menuGroup.getId(),
                menuGroup.getName(),
                menuGroup.getMenuItems()
                        .stream()
                        .map(MenuItemMapper::toDto)
                        .collect(Collectors.toList())
        );
    }
}
