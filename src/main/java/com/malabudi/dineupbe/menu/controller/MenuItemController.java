package com.malabudi.dineupbe.menu.controller;

import com.malabudi.dineupbe.menu.dto.MenuItemDto;
import com.malabudi.dineupbe.menu.service.MenuItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MenuItemDto> addMenuItem(
            @RequestBody MenuItemDto menuItemDto
    ) {
        MenuItemDto res = menuItemService.addMenuItem(menuItemDto);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MenuItemDto> updateMenuItem(
            @PathVariable Integer id,
            @RequestBody MenuItemDto menuItemDto
    ) {

        MenuItemDto res = menuItemService.updateMenuItem(id, menuItemDto);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteMenuItem(
            @PathVariable Integer id
    ) {
        menuItemService.deleteMenuItem(id);
        return new ResponseEntity<>("Menu item with id " + id + " deleted", HttpStatus.OK);
    }
}
