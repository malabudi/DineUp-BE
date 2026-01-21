package com.malabudi.dineupbe.menu.controller;

import com.malabudi.dineupbe.menu.dto.MenuItemDto;
import com.malabudi.dineupbe.menu.service.MenuItemService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/menu-items")
public class MenuItemController {
    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @GetMapping
    public List<MenuItemDto> getMenuItem() {
        return menuItemService.getAllMenuItems();
    }

    @GetMapping("{id}")
    public MenuItemDto getMenuItem(
            @PathVariable Integer id
    ) {
        return menuItemService.getMenuItemById(id);
    }

    @PostMapping
    public void addMenuItem(
            @RequestBody MenuItemDto menuItemDto
    ) {
        menuItemService.addMenuItem(menuItemDto);
    }
}
