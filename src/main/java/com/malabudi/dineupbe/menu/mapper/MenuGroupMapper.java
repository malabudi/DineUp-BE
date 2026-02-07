package com.malabudi.dineupbe.menu.mapper;

import com.malabudi.dineupbe.menu.dto.ResponseMenuGroupDto;
import com.malabudi.dineupbe.menu.model.MenuGroup;

import java.util.stream.Collectors;

public class MenuGroupMapper {
    public static ResponseMenuGroupDto toDto(MenuGroup menuGroup) {
        return new ResponseMenuGroupDto(
                menuGroup.getId(),
                menuGroup.getName(),
                menuGroup.getMenuItems()
                        .stream()
                        .map(MenuItemMapper::toDto)
                        .collect(Collectors.toList())
        );
    }
}
