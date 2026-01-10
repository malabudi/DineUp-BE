package com.example.dineupbe;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuItemService {
    private final MenuItemRepository menuItemRepository;
    private final MenuItemDTOMapper menuItemDTOMapper;
    private final MenuItemEntityMapper menuItemEntityMapper;

    public MenuItemService(
            MenuItemRepository menuItemRepository,
            MenuItemDTOMapper menuItemDTOMapper,
            MenuItemEntityMapper menuItemEntityMapper) {
        this.menuItemRepository = menuItemRepository;
        this.menuItemDTOMapper = menuItemDTOMapper;
        this.menuItemEntityMapper = menuItemEntityMapper;
    }

    public List<MenuItemDto> getAllMenuItems() {
        return menuItemRepository.findAll()
                .stream()
                .map(menuItemDTOMapper)
                .collect(Collectors.toList());
    }

    public MenuItemDto getMenuItemById(Integer id) {
        return menuItemDTOMapper.apply(
                menuItemRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                        id + " not found"))
        );
    }

    public void insertMenuItem(MenuItemDto menuItemDto) {
        MenuItem menuItem = menuItemEntityMapper.apply(menuItemDto);
        menuItemRepository.save(menuItem);
    }
}
