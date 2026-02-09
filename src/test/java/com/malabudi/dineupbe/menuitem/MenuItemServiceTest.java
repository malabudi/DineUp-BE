package com.malabudi.dineupbe.menuitem;

import com.malabudi.dineupbe.menu.dto.RequestMenuItemDto;
import com.malabudi.dineupbe.menu.dto.ResponseMenuItemDto;
import com.malabudi.dineupbe.menu.exception.InvalidMenuItemException;
import com.malabudi.dineupbe.menu.exception.MenuGroupNotFoundException;
import com.malabudi.dineupbe.menu.exception.MenuItemNotFoundException;
import com.malabudi.dineupbe.menu.model.MenuGroup;
import com.malabudi.dineupbe.menu.model.MenuItem;
import com.malabudi.dineupbe.menu.repository.MenuGroupRepository;
import com.malabudi.dineupbe.menu.repository.MenuItemRepository;
import com.malabudi.dineupbe.menu.service.MenuItemService;
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

    @BeforeEach
    void setUp() {
        underTest = new MenuItemService(menuItemRepository, menuGroupRepository);
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
        MenuGroup dummyMenuGroup = new MenuGroup("Main Course");
        dummyMenuGroup.setId(1L);

        Long menuItemId = 1L;
        MenuItem menuItem = new MenuItem(
                "Hamburger",
                "Tasty burger",
                new BigDecimal("8.99"),
                null
        );
        menuItem.setMenuGroup(dummyMenuGroup);
        menuItem.setId(menuItemId);

        when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));

        // Act
        ResponseMenuItemDto responseMenuItemDto = underTest.getMenuItemById(menuItem.getId());

        // Assert
        verify(menuItemRepository).findById(menuItemId);
        assertThat(responseMenuItemDto.name()).isEqualTo("Hamburger");
        assertThat(responseMenuItemDto.description()).isEqualTo("Tasty burger");
        assertThat(responseMenuItemDto.price()).isEqualTo(new BigDecimal("8.99"));
    }

    @Test
    void getMenuItemById_shouldThrowException_whenIdNotFound() {
        // Arrange
        Long menuItemId = 1L;

        when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> underTest.getMenuItemById(menuItemId))
                .isInstanceOf(MenuItemNotFoundException.class)
                .hasMessage("Menu item not found");
    }

    @Test
    void addMenuItem_shouldSaveMenuItem_whenValidMenuItemSent() {
        // Arrange
        MenuGroup dummyMenuGroup = new MenuGroup("Main Course");
        dummyMenuGroup.setId(1L);

        when(
                menuGroupRepository.findById(1L))
                .thenReturn(Optional.of(dummyMenuGroup)
        );

        RequestMenuItemDto request = new RequestMenuItemDto(
                1L,
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
        Long menuGroupId = 1000L;
        RequestMenuItemDto request = new RequestMenuItemDto(
                menuGroupId,
                "Pizza",
                "Cheese and dough",
                new BigDecimal("8.99"),
                null);

        when(menuGroupRepository.findById(menuGroupId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> underTest.addMenuItem(request))
                .isInstanceOf(MenuGroupNotFoundException.class)
                .hasMessage("Menu group with id " + menuGroupId + " not found");

        verify(menuItemRepository, never()).save(any());
    }

    @Test
    void addMenuItem_shouldThrowException_whenMenuGroupIdNull() {
        // Arrange
        RequestMenuItemDto request = new RequestMenuItemDto(
                null,
                "Pizza",
                "Cheese and dough",
                new BigDecimal("8.99"),
                null);

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

        MenuGroup oldMenuGroup = new MenuGroup("Old Menu Group");
        oldMenuGroup.setId(1L);

        Long existingMenuItemId = 3L;
        MenuItem existingMenuItem = new MenuItem(
                "Pizza",
                "Cheese and Dough",
                new BigDecimal("8.99"),
                null
        );
        existingMenuItem.setMenuGroup(oldMenuGroup);

        RequestMenuItemDto request = new RequestMenuItemDto(
                newMenuGroupId,
                "Pizza",
                "Cheese and Dough",
                new BigDecimal("8.99"),
                null
        );

        when(menuItemRepository.findById(existingMenuItemId)).thenReturn(Optional.of(existingMenuItem));
        when(menuGroupRepository.findById(newMenuGroupId)).thenReturn(Optional.of(newMenuGroup));
        when(menuItemRepository.save(existingMenuItem)).thenReturn(existingMenuItem);

        ArgumentCaptor<MenuItem> captor = ArgumentCaptor.forClass(MenuItem.class);

        // Act
        underTest.updateMenuItem(existingMenuItemId, request);

        // Assert
        verify(menuItemRepository).save(captor.capture());
        MenuItem capturedMenuItem = captor.getValue();

        assertThat(capturedMenuItem.getMenuGroup().getId()).isEqualTo(newMenuGroupId);
        assertThat(capturedMenuItem.getName()).isEqualTo("Pizza");
        assertThat(capturedMenuItem.getDescription()).isEqualTo("Cheese and Dough");
    }

    @Test
    void updateMenuItem_shouldUpdateMenuGroup_whenMenuItemIsDifferent() {
        // Arrange
        MenuGroup menuGroup = new MenuGroup("Pizza Menu Group");
        Long menuGroupId = 1L;
        menuGroup.setId(menuGroupId);

        Long menuItemId = 1L;
        MenuItem oldMenuItem = new MenuItem(
                "Pizza",
                "Cheese and Dough",
                new BigDecimal("8.99"),
                null
        );
        oldMenuItem.setMenuGroup(menuGroup);

        RequestMenuItemDto request = new RequestMenuItemDto(
                menuGroupId,
                "New Pizza",
                "New Cheese and Dough",
                new BigDecimal("18.99"),
                null
        );

        MenuItem newMenuItem = new MenuItem(
                "New Pizza",
                "New Cheese and Dough",
                new BigDecimal("18.99"),
                null
        );
        newMenuItem.setMenuGroup(menuGroup);

        when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.of(oldMenuItem));
        when(menuItemRepository.save(any())).thenReturn(newMenuItem);

        ArgumentCaptor<MenuItem> captor = ArgumentCaptor.forClass(MenuItem.class);

        // Act
        underTest.updateMenuItem(menuItemId, request);

        // Assert
        verify(menuItemRepository).save(captor.capture());
        MenuItem menuItem = captor.getValue();

        assertThat(menuItem.getMenuGroup().getId()).isEqualTo(menuGroupId);
        assertThat(menuItem.getMenuGroup().getName()).isEqualTo("Pizza Menu Group");
        assertThat(menuItem.getName()).isEqualTo("New Pizza");
    }

    @Test
    void updateMenuItem_shouldThrowException_whenMenuItemNotFound() {
        // Arrange
        Long menuItemId = 1L;
        when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> underTest.updateMenuItem(menuItemId, new RequestMenuItemDto(
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
        Long existingMenuItemId = 1L;
        when(menuItemRepository.existsById(existingMenuItemId)).thenReturn(true);

        // Act
        underTest.deleteMenuItem(existingMenuItemId);

        // Assert
        verify(menuItemRepository).deleteById(existingMenuItemId);
    }

    @Test
    void deleteMenuItem_shouldThrowException_whenMenuItemNotFound() {
        // Arrange
        Long existingMenuItemId = 1L;
        when(menuItemRepository.existsById(existingMenuItemId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> underTest.deleteMenuItem(existingMenuItemId))
                .isInstanceOf(MenuItemNotFoundException.class);

        verify(menuItemRepository, never()).deleteById(any(Long.class));
    }
}
