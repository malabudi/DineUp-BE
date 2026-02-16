package com.dineup.menu.mapper;

import com.dineup.menu.dto.ResponseMenuGroupDto;
import com.dineup.menu.model.MenuGroup;

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
