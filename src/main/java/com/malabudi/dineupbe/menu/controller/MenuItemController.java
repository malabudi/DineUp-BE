package com.malabudi.dineupbe.menu.controller;

import com.malabudi.dineupbe.menu.dto.MenuItemDto;
import com.malabudi.dineupbe.menu.service.MenuItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<MenuItemDto>> getMenuItem() {
        List<MenuItemDto> menuItems = menuItemService.getAllMenuItems();
        return  new ResponseEntity<>(menuItems, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<MenuItemDto> getMenuItem(
            @PathVariable Integer id
    ) {
        return new ResponseEntity<>(menuItemService.getMenuItemById(id),  HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> addMenuItem(
            @RequestBody MenuItemDto menuItemDto
    ) {
        menuItemService.addMenuItem(menuItemDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
