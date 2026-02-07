package com.malabudi.dineupbe.menu.controller;

import com.malabudi.dineupbe.menu.dto.RequestMenuItemDto;
import com.malabudi.dineupbe.menu.dto.ResponseMenuItemDto;
import com.malabudi.dineupbe.menu.service.MenuItemService;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<ResponseMenuItemDto>> getMenuItem() {
        List<ResponseMenuItemDto> res = menuItemService.getAllMenuItems();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<ResponseMenuItemDto> getMenuItem(
            @PathVariable Long id
    ) {
        ResponseMenuItemDto res = menuItemService.getMenuItemById(id);
        return new ResponseEntity<>(res,  HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMenuItemDto> addMenuItem(
            @Valid @RequestBody RequestMenuItemDto requestMenuItemDto
    ) {
        ResponseMenuItemDto res = menuItemService.addMenuItem(requestMenuItemDto);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMenuItemDto> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody RequestMenuItemDto requestMenuItemDto
    ) {

        ResponseMenuItemDto res = menuItemService.updateMenuItem(id, requestMenuItemDto);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteMenuItem(
            @PathVariable Long id
    ) {
        menuItemService.deleteMenuItem(id);
        return new ResponseEntity<>("Menu item with id " + id + " deleted", HttpStatus.OK);
    }
}
