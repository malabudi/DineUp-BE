package com.malabudi.dineupbe.menu.service;

import com.malabudi.dineupbe.menu.exception.InvalidMenuItemException;
import com.malabudi.dineupbe.menu.exception.MenuItemNotFoundException;
import com.malabudi.dineupbe.menu.model.MenuGroup;
import com.malabudi.dineupbe.menu.model.MenuItem;
import com.malabudi.dineupbe.menu.dto.MenuItemDto;
import com.malabudi.dineupbe.menu.mapper.MenuItemMapper;
import com.malabudi.dineupbe.menu.repository.MenuGroupRepository;
import com.malabudi.dineupbe.menu.repository.MenuItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MenuItemService {
    private final MenuItemRepository menuItemRepository;
    private final MenuGroupRepository menuGroupRepository;

    public MenuItemService(
            MenuItemRepository menuItemRepository,
            MenuItemMapper menuItemMapper,
            MenuGroupRepository menuGroupRepository
    ) {
        this.menuItemRepository = menuItemRepository;
        this.menuGroupRepository = menuGroupRepository;
    }

    private MenuGroup checkMenuGroup(Long menuGroupId) {
        if (menuGroupId == null) {
            throw new InvalidMenuItemException("Menu group id is required");
        }

        return  menuGroupRepository.findById(menuGroupId).orElseThrow(
                () -> new MenuItemNotFoundException("Menu group with id " + menuGroupId + " not found")
        );
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
                .map(MenuItemMapper::toDto)
                .collect(Collectors.toList());
    }

    public MenuItemDto getMenuItemById(Long id) {
        return MenuItemMapper.toDto(
                menuItemRepository.findById(id).orElseThrow(MenuItemNotFoundException::new)
        );
    }

    public MenuItemDto addMenuItem(MenuItemDto menuItemDto) {
        this.validateMenuItem(menuItemDto);

        MenuGroup menuGroup = checkMenuGroup(menuItemDto.menuGroupId());

        MenuItem menuItem = new MenuItem(
                menuItemDto.name(),
                menuItemDto.description(),
                menuItemDto.price(),
                menuItemDto.imageUrl()
        );

        menuItem.setMenuGroup(menuGroup);
        menuItemRepository.save(menuItem);

        return  MenuItemMapper.toDto(menuItem);
    }

    public MenuItemDto updateMenuItem(Long id, MenuItemDto menuItemDto) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(MenuItemNotFoundException::new);

        validateMenuItem(menuItemDto);

        menuItem.setName(menuItemDto.name());
        menuItem.setDescription(menuItemDto.description());
        menuItem.setPrice(menuItemDto.price());
        menuItem.setImageUrl(menuItemDto.imageUrl());

        // Update menu group ID if menu item is in a different group
        if (
                menuItemDto.menuGroupId() != null &&
                !Objects.equals(menuItem.getMenuGroup().getId(), menuItemDto.menuGroupId())
        ) {
            MenuGroup menuGroup = checkMenuGroup(menuItemDto.menuGroupId());
            menuItem.setMenuGroup(menuGroup);
        }

        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);
        return MenuItemMapper.toDto(updatedMenuItem);
    }

    public void deleteMenuItem(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new  MenuItemNotFoundException(id);
        }

        menuItemRepository.deleteById(id);
    }
}
