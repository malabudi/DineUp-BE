package com.malabudi.dineupbe.menu.service;

import com.malabudi.dineupbe.menu.dto.RequestMenuItemDto;
import com.malabudi.dineupbe.menu.dto.ResponseMenuItemDto;
import com.malabudi.dineupbe.menu.exception.InvalidMenuItemException;
import com.malabudi.dineupbe.menu.exception.MenuGroupNotFoundException;
import com.malabudi.dineupbe.menu.exception.MenuItemNotFoundException;
import com.malabudi.dineupbe.menu.model.MenuGroup;
import com.malabudi.dineupbe.menu.model.MenuItem;
import com.malabudi.dineupbe.menu.repository.MenuGroupRepository;
import com.malabudi.dineupbe.menu.repository.MenuItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuItemServiceTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    private MenuItemService underTest;
    private MenuGroup defaultMenuGroup;
    private MenuItem defaultMenuItem;

    private final Long MENU_GROUP_ID = 1L;
    private final Long MENU_ITEM_ID = 1L;

    @BeforeEach
    void setUp() {
        underTest = new MenuItemService(menuItemRepository, menuGroupRepository);

        defaultMenuGroup = new MenuGroup("Main Course");
        defaultMenuGroup.setId(MENU_GROUP_ID);

        defaultMenuItem = new MenuItem(
                "Hamburger",
                "Tasty burger",
                new BigDecimal("8.99"),
                null
        );
        defaultMenuItem.setId(MENU_ITEM_ID);
        defaultMenuItem.setMenuGroup(defaultMenuGroup);
    }

    @Test
    void getAllMenuItems_shouldReturnAllMenuItems_whenCalled() {
        // When
        underTest.getAllMenuItems();

        // Then
        verify(menuItemRepository).findAll();
    }

    @Test
    void getMenuItemById_shouldReturnMenuItemById_whenIdExists() {
        // Arrange
        when(menuItemRepository.findById(MENU_ITEM_ID)).thenReturn(Optional.of(defaultMenuItem));

        // Act
        ResponseMenuItemDto responseMenuItemDto = underTest.getMenuItemById(defaultMenuItem.getId());

        // Assert
        verify(menuItemRepository).findById(MENU_ITEM_ID);
        assertThat(responseMenuItemDto.name()).isEqualTo("Hamburger");
        assertThat(responseMenuItemDto.description()).isEqualTo("Tasty burger");
        assertThat(responseMenuItemDto.price()).isEqualTo(new BigDecimal("8.99"));
    }

    @Test
    void getMenuItemById_shouldThrowException_whenIdNotFound() {
        // Arrange
        when(menuItemRepository.findById(MENU_ITEM_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> underTest.getMenuItemById(MENU_ITEM_ID))
                .isInstanceOf(MenuItemNotFoundException.class)
                .hasMessage("Menu item not found");
    }

    @Test
    void addMenuItem_shouldSaveMenuItem_whenValidMenuItemSent() {
        // Arrange
        when(
                menuGroupRepository.findById(MENU_GROUP_ID))
                .thenReturn(Optional.of(defaultMenuGroup)
        );

        RequestMenuItemDto request = new RequestMenuItemDto(
                MENU_GROUP_ID,
                "Hamburger",
                "Tasty burger",
                new BigDecimal("8.99"),
                null
        );

        ArgumentCaptor<MenuItem> menuItemArgumentCaptor = ArgumentCaptor.forClass(MenuItem.class);

        // Act
        underTest.addMenuItem(request);

        // Assert
        verify(menuItemRepository).save(menuItemArgumentCaptor.capture());
        MenuItem capturedMenuItem = menuItemArgumentCaptor.getValue();

        assertThat(capturedMenuItem.getName()).isEqualTo("Hamburger");
        assertThat(capturedMenuItem.getDescription()).isEqualTo("Tasty burger");
        assertThat(capturedMenuItem.getPrice()).isEqualTo(new BigDecimal("8.99"));
    }

    @Test
    void addMenuItem_shouldThrowException_whenMenuGroupNotFound() {
        // Arrange
        RequestMenuItemDto request = new RequestMenuItemDto(
                MENU_GROUP_ID,
                "Hamburger",
                "Tasty burger",
                new BigDecimal("8.99"),
                null
        );

        when(menuGroupRepository.findById(MENU_GROUP_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> underTest.addMenuItem(request))
                .isInstanceOf(MenuGroupNotFoundException.class)
                .hasMessage("Menu group with id " + MENU_GROUP_ID + " not found");

        verify(menuItemRepository, never()).save(any());
    }

    @Test
    void addMenuItem_shouldThrowException_whenMenuGroupIdNull() {
        // Arrange
        RequestMenuItemDto request = new RequestMenuItemDto(
                null,
                "Hamburger",
                "Tasty burger",
                new BigDecimal("8.99"),
                null
        );

        // Act & Assert
        assertThatThrownBy(() -> underTest.addMenuItem(request))
                .isInstanceOf(InvalidMenuItemException.class)
                .hasMessage("Menu group id is required");

        verify(menuItemRepository, never()).save(any());
    }

    @Test
    void updateMenuItem_shouldUpdateMenuGroup_whenMenuItemInDifferentGroup() {
        // Arrange
        Long newMenuGroupId = 2L;
        MenuGroup newMenuGroup = new MenuGroup("New Menu Group");
        newMenuGroup.setId(newMenuGroupId);

        RequestMenuItemDto request = new RequestMenuItemDto(
                newMenuGroupId,
                "Hamburger",
                "Tasty burger",
                new BigDecimal("8.99"),
                null
        );

        when(menuItemRepository.findById(MENU_ITEM_ID)).thenReturn(Optional.of(defaultMenuItem));
        when(menuGroupRepository.findById(newMenuGroupId)).thenReturn(Optional.of(newMenuGroup));
        when(menuItemRepository.save(defaultMenuItem)).thenReturn(defaultMenuItem);

        ArgumentCaptor<MenuItem> captor = ArgumentCaptor.forClass(MenuItem.class);

        // Act
        underTest.updateMenuItem(MENU_ITEM_ID, request);

        // Assert
        verify(menuItemRepository).save(captor.capture());
        MenuItem capturedMenuItem = captor.getValue();

        assertThat(capturedMenuItem.getMenuGroup().getId()).isEqualTo(newMenuGroupId);
        assertThat(capturedMenuItem.getName()).isEqualTo("Hamburger");
        assertThat(capturedMenuItem.getDescription()).isEqualTo("Tasty burger");
    }

    @Test
    void updateMenuItem_shouldMenuItem_whenMenuItemIsDifferent() {
        // Arrange
        RequestMenuItemDto request = new RequestMenuItemDto(
                MENU_GROUP_ID,
                "Pizza",
                "Cheese and Dough",
                new BigDecimal("18.99"),
                null
        );

        when(menuItemRepository.findById(MENU_ITEM_ID)).thenReturn(Optional.of(defaultMenuItem));
        when(menuItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ArgumentCaptor<MenuItem> captor = ArgumentCaptor.forClass(MenuItem.class);

        // Act
        underTest.updateMenuItem(MENU_ITEM_ID, request);

        // Assert
        verify(menuItemRepository).save(captor.capture());
        MenuItem menuItem = captor.getValue();

        assertThat(menuItem.getMenuGroup().getId()).isEqualTo(MENU_GROUP_ID);
        assertThat(menuItem.getMenuGroup().getName()).isEqualTo("Main Course");
        assertThat(menuItem.getName()).isEqualTo("Pizza");
        assertThat( menuItem.getDescription()).isEqualTo("Cheese and Dough");
    }

    @Test
    void updateMenuItem_shouldThrowException_whenMenuItemNotFound() {
        // Arrange
        when(menuItemRepository.findById(MENU_ITEM_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> underTest.updateMenuItem(MENU_ITEM_ID, new RequestMenuItemDto(
                1L,
                "Pizza",
                "Cheese and Dough",
                new BigDecimal("8.99"),
                null
        )))
                .isInstanceOf(MenuItemNotFoundException.class)
                .hasMessage("Menu item not found");

        verify(menuItemRepository, never()).save(any());
    }

    @Test
    void deleteMenuItem_shouldDeleteMenuItem_whenMenuItemFound() {
        // Arrange
        when(menuItemRepository.existsById(MENU_ITEM_ID)).thenReturn(true);

        // Act
        underTest.deleteMenuItem(MENU_ITEM_ID);

        // Assert
        verify(menuItemRepository).deleteById(MENU_ITEM_ID);
    }

    @Test
    void deleteMenuItem_shouldThrowException_whenMenuItemNotFound() {
        // Arrange
        when(menuItemRepository.existsById(MENU_ITEM_ID)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> underTest.deleteMenuItem(MENU_ITEM_ID))
                .isInstanceOf(MenuItemNotFoundException.class);

        verify(menuItemRepository, never()).deleteById(any(Long.class));
    }
}
