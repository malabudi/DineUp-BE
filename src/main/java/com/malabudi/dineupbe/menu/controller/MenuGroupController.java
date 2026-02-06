package com.malabudi.dineupbe.menu.controller;

import com.malabudi.dineupbe.menu.dto.CreateMenuGroupDto;
import com.malabudi.dineupbe.menu.dto.MenuGroupDto;
import com.malabudi.dineupbe.menu.dto.UpdateMenuGroupDto;
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
        List<MenuGroupDto> res = menuGroupService.getMenuGroups();
        return new  ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<MenuGroupDto> getMenuGroupById(
            @PathVariable Long id
    ) {
        MenuGroupDto menuGroupDto = menuGroupService.getMenuGroupById(id);
        return new  ResponseEntity<>(menuGroupDto, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MenuGroupDto> createMenuGroup(
            @RequestBody CreateMenuGroupDto createMenuGroupDto
    ) {
        MenuGroupDto res = menuGroupService.addMenuGroup(createMenuGroupDto);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MenuGroupDto> updateMenuGroupByName(
            @PathVariable Long id,
            @RequestBody UpdateMenuGroupDto updateMenuGroupDto
    ) {
        MenuGroupDto res = menuGroupService.updateMenuGroupName(id, updateMenuGroupDto);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteMenuGroup(
            @PathVariable Long id
    ) {
        menuGroupService.deleteMenuGroup(id);
        return new ResponseEntity<>("Menu group with id " + id + " deleted", HttpStatus.OK);
    }
}
