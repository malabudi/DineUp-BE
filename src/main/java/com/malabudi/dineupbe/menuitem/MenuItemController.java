package com.malabudi.dineupbe.menuitem;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/v1/menu-items")
public class MenuItemController {
    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @GetMapping
    public List<MenuItemDto> getMenuItems() {
        return menuItemService.getAllMenuItems();
    }

    @GetMapping("{id}")
    public MenuItemDto getMenuItems(
            @PathVariable Integer id
    ) {
        return menuItemService.getMenuItemById(id);
    }

    @PostMapping
    public void addNewMenuItem(
            @RequestBody MenuItemDto menuItemDto
            ) {
        menuItemService.insertMenuItem(menuItemDto);
    }
}
