package com.malabudi.dineupbe.menu.controller;

import com.malabudi.dineupbe.menu.dto.MenuGroupDto;
import com.malabudi.dineupbe.menu.service.MenuGroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu-group")
public class MenuGroupController {
    private final MenuGroupService menuGroupService;

    public MenuGroupController(MenuGroupService menuGroupService) {
        this.menuGroupService = menuGroupService;
    }

    @GetMapping
    public List<MenuGroupDto> getMenuGroups() {
        return null;
    }

    @GetMapping("{name}")
    public List<MenuGroupDto> getMenuGroupByName(
            @PathVariable String name
    ) {
        return null;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createMenuGroup(
            @RequestBody MenuGroupDto menuGroupDto
    ) {
        return new ResponseEntity<>("Menu group created successfully", HttpStatus.CREATED);
    }

    @PatchMapping("{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateMenuGroupByName(
            @PathVariable String name
    ) {
        return new ResponseEntity<>("Menu group name updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteMenuGroup(
            @PathVariable String name
    ) {
        return new ResponseEntity<>("Menu group delete successfully", HttpStatus.OK);
    }
}
