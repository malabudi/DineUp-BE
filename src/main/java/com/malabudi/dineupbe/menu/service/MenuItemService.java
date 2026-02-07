package com.malabudi.dineupbe.menu.service;

import com.malabudi.dineupbe.menu.dto.ResponseMenuItemDto;
import com.malabudi.dineupbe.menu.exception.InvalidMenuItemException;
import com.malabudi.dineupbe.menu.exception.MenuItemNotFoundException;
import com.malabudi.dineupbe.menu.model.MenuGroup;
import com.malabudi.dineupbe.menu.model.MenuItem;
import com.malabudi.dineupbe.menu.dto.RequestMenuItemDto;
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

    public List<ResponseMenuItemDto> getAllMenuItems() {
        return menuItemRepository.findAll()
                .stream()
                .map(MenuItemMapper::toDto)
                .collect(Collectors.toList());
    }

    public ResponseMenuItemDto getMenuItemById(Long id) {
        return MenuItemMapper.toDto(
                menuItemRepository.findById(id).orElseThrow(MenuItemNotFoundException::new)
        );
    }

    public ResponseMenuItemDto addMenuItem(RequestMenuItemDto requestMenuItemDto) {
        MenuGroup menuGroup = checkMenuGroup(requestMenuItemDto.menuGroupId());

        MenuItem menuItem = new MenuItem(
                requestMenuItemDto.name(),
                requestMenuItemDto.description(),
                requestMenuItemDto.price(),
                requestMenuItemDto.imageUrl()
        );

        menuItem.setMenuGroup(menuGroup);
        menuItemRepository.save(menuItem);

        return  MenuItemMapper.toDto(menuItem);
    }

    public ResponseMenuItemDto updateMenuItem(Long id, RequestMenuItemDto requestMenuItemDto) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(MenuItemNotFoundException::new);

        menuItem.setName(requestMenuItemDto.name());
        menuItem.setDescription(requestMenuItemDto.description());
        menuItem.setPrice(requestMenuItemDto.price());
        menuItem.setImageUrl(requestMenuItemDto.imageUrl());

        // Update menu group ID if menu item is in a different group
        if (
                requestMenuItemDto.menuGroupId() != null &&
                !Objects.equals(menuItem.getMenuGroup().getId(), requestMenuItemDto.menuGroupId())
        ) {
            MenuGroup menuGroup = checkMenuGroup(requestMenuItemDto.menuGroupId());
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
