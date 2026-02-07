package com.malabudi.dineupbe.menu.dto;

import java.util.List;

public record ResponseMenuGroupDto(
        Long id,
        String name,
        List<ResponseMenuItemDto> items
) { }
