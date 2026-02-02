package com.malabudi.dineupbe.menu.mapper;

import com.malabudi.dineupbe.menu.dto.MenuGroupDto;
import com.malabudi.dineupbe.menu.model.MenuGroup;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MenuGroupMapper {
    private final MenuItemMapper menuItemMapper;

    public MenuGroupMapper(MenuItemMapper menuItemMapper) {
        this.menuItemMapper = menuItemMapper;
    }

    public MenuGroupDto toDto(MenuGroup menuGroup) {
        return new MenuGroupDto(
                menuGroup.getId(),
                menuGroup.getName(),
                menuGroup.getMenuItems()
                        .stream()
                        .map(menuItemMapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    public MenuGroup toEntity(MenuGroupDto menuGroupDto) {
        return new MenuGroup(
          menuGroupDto.name(),
          menuGroupDto.items()
                  .stream()
                  .map(menuItemMapper::toEntity)
                  .collect(Collectors.toList())
        );
    }
}
