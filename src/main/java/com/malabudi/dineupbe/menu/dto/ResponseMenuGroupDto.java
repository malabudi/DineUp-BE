package com.malabudi.dineupbe.menu.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record ResponseMenuGroupDto(
        Long id,
        String name,
        List<MenuItemDto> items
) { }
