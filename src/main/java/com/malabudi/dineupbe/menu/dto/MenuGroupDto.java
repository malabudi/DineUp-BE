package com.malabudi.dineupbe.menu.dto;

import java.util.List;

public record MenuGroupDto(
        Integer id,
        String name,
        List<MenuItemDto> items
) { }
