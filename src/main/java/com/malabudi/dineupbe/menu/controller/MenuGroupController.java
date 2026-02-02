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
    public ResponseEntity<List<MenuGroupDto>> getMenuGroups() {
        List<MenuGroupDto> menuGroupDto = menuGroupService.getMenuGroups();
        return new  ResponseEntity<>(menuGroupDto, HttpStatus.OK);
    }

    @GetMapping("{name}")
    public ResponseEntity<MenuGroupDto> getMenuGroupByName(
            @PathVariable String name
    ) {
        MenuGroupDto menuGroupDto = menuGroupService.getMenuGroupByName(name);
        return new  ResponseEntity<>(menuGroupDto, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MenuGroupDto> createMenuGroup(
            @RequestBody MenuGroupDto menuGroupDto
    ) {
        menuGroupService.addMenuGroup(menuGroupDto);
        return new ResponseEntity<>(menuGroupDto, HttpStatus.CREATED);
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
