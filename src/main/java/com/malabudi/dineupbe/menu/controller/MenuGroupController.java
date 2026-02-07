package com.malabudi.dineupbe.menu.controller;

import com.malabudi.dineupbe.menu.dto.CreateMenuGroupDto;
import com.malabudi.dineupbe.menu.dto.ResponseMenuGroupDto;
import com.malabudi.dineupbe.menu.dto.UpdateMenuGroupDto;
import com.malabudi.dineupbe.menu.service.MenuGroupService;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<ResponseMenuGroupDto>> getMenuGroups() {
        List<ResponseMenuGroupDto> res = menuGroupService.getMenuGroups();
        return new  ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<ResponseMenuGroupDto> getMenuGroupById(
            @PathVariable Long id
    ) {
        ResponseMenuGroupDto responseMenuGroupDto = menuGroupService.getMenuGroupById(id);
        return new  ResponseEntity<>(responseMenuGroupDto, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMenuGroupDto> createMenuGroup(
            @Valid @RequestBody CreateMenuGroupDto createMenuGroupDto
    ) {
        ResponseMenuGroupDto res = menuGroupService.addMenuGroup(createMenuGroupDto);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMenuGroupDto> updateMenuGroupByName(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMenuGroupDto updateMenuGroupDto
    ) {
        ResponseMenuGroupDto res = menuGroupService.updateMenuGroupName(id, updateMenuGroupDto);
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
