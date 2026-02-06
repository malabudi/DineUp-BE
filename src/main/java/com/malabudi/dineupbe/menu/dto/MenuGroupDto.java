package com.malabudi.dineupbe.menu.dto;

import java.util.List;

public record MenuGroupDto(
        Long id,
        String name,
        List<MenuItemDto> items
) { }
