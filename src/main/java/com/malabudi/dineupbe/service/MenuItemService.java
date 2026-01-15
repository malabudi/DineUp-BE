package com.malabudi.dineupbe.service;

import com.malabudi.dineupbe.model.MenuItem;
import com.malabudi.dineupbe.dto.MenuItemDto;
import com.malabudi.dineupbe.mapper.MenuItemMapper;
import com.malabudi.dineupbe.repository.MenuItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuItemService {
    private final MenuItemRepository menuItemRepository;
    private final MenuItemMapper menuItemMapper;

    public MenuItemService(
            MenuItemRepository menuItemRepository,
            MenuItemMapper menuItemMapper) {
        this.menuItemRepository = menuItemRepository;
        this.menuItemMapper = menuItemMapper;
    }

    public List<MenuItemDto> getAllMenuItems() {
        return menuItemRepository.findAll()
                .stream()
                .map(menuItemMapper::toDto)
                .collect(Collectors.toList());
    }

    public MenuItemDto getMenuItemById(Integer id) {
        return menuItemMapper.toDto(
                menuItemRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                        id + " not found"))
        );
    }

    public void insertMenuItem(MenuItemDto menuItemDto) {
        MenuItem menuItem = menuItemMapper.toEntity(menuItemDto);
        menuItemRepository.save(menuItem);
    }
}
