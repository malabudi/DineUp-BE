package com.malabudi.dineupbe.menu.service;

import com.malabudi.dineupbe.menu.exception.DuplicateMenuItemException;
import com.malabudi.dineupbe.menu.exception.InvalidMenuItemException;
import com.malabudi.dineupbe.menu.exception.MenuItemNotFoundException;
import com.malabudi.dineupbe.menu.model.MenuItem;
import com.malabudi.dineupbe.menu.dto.MenuItemDto;
import com.malabudi.dineupbe.menu.mapper.MenuItemMapper;
import com.malabudi.dineupbe.menu.repository.MenuItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    private void checkMenuItemExists(MenuItemDto menuItemDto) {
        if (menuItemRepository.findByName(menuItemDto.name()).isPresent()) {
            throw new DuplicateMenuItemException("Menu item with name " + menuItemDto.name() + " already exists");
        }
    }

    private void validateMenuItem(MenuItemDto menuItemDto) {
        // ToDo - Validate the image after S3 is hooked up
        // Is menu item name null
        if (
                menuItemDto.name() == null ||
                        menuItemDto.name().trim().isEmpty()
        ) {
            throw new InvalidMenuItemException("Menu item name cannot be empty");
        }

        // Is menu item price invalid
        if (
                menuItemDto.price() == null ||
                        menuItemDto.price().compareTo(BigDecimal.ZERO) <= 0
        ) {
            throw new InvalidMenuItemException("Menu item price must be greater than zero");
        }

        // Is menu item description valid
        if (
                menuItemDto.description() == null ||
                        menuItemDto.description().trim().isEmpty()
        ) {
            throw new InvalidMenuItemException("Menu item description cannot be empty");
        }
    }

    public List<MenuItemDto> getAllMenuItems() {
        return menuItemRepository.findAll()
                .stream()
                .map(menuItemMapper::toDto)
                .collect(Collectors.toList());
    }

    public MenuItemDto getMenuItemById(Integer id) {
        return menuItemMapper.toDto(
                menuItemRepository.findById(id).orElseThrow(MenuItemNotFoundException::new)
        );
    }

    public MenuItemDto addMenuItem(MenuItemDto menuItemDto) {
        this.validateMenuItem(menuItemDto);
        this.checkMenuItemExists(menuItemDto);

        MenuItem mappedMenuItem = menuItemMapper.toEntity(menuItemDto);
        MenuItem savedMenuItem = menuItemRepository.save(mappedMenuItem);
        return  menuItemMapper.toDto(savedMenuItem);
    }

    public MenuItemDto updateMenuItem(Integer id, MenuItemDto menuItemDto) {
        MenuItem menuItem = menuItemRepository.findById(id).orElseThrow(MenuItemNotFoundException::new);

        validateMenuItem(menuItemDto);

        // Only check if name changes
        if  (!menuItem.getName().equals(menuItemDto.name())) {
            checkMenuItemExists(menuItemDto);
        }

        menuItem.setName(menuItemDto.name());
        menuItem.setDescription(menuItemDto.description());
        menuItem.setPrice(menuItemDto.price());
        menuItem.setImageUrl(menuItemDto.imageUrl());

        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);
        return menuItemMapper.toDto(updatedMenuItem);
    }

    public void deleteMenuItem(Integer id) {
        if (!menuItemRepository.existsById(id)) {
            throw new  MenuItemNotFoundException(id);
        }

        menuItemRepository.deleteById(id);
    }
}
