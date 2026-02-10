package com.malabudi.dineupbe.menu.service;

import com.malabudi.dineupbe.menu.dto.CreateMenuGroupDto;
import com.malabudi.dineupbe.menu.dto.UpdateMenuGroupDto;
import com.malabudi.dineupbe.menu.exception.MenuGroupNotFoundException;
import com.malabudi.dineupbe.menu.model.MenuGroup;
import com.malabudi.dineupbe.menu.repository.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuGroupServiceTest {
    @Mock
    private MenuGroupRepository menuGroupRepository;

    private MenuGroupService underTest;
    private MenuGroup defaultMenuGroup;

    private final Long MENU_GROUP_ID = 1L;

    @BeforeEach
    public void setUp() {
        underTest = new MenuGroupService(menuGroupRepository);

        defaultMenuGroup = new MenuGroup("Main Course");
        defaultMenuGroup.setId(MENU_GROUP_ID);
    }

    @Test
    void getAllMenuGroups_shouldReturnAllMenuGroups_whenCalled() {
        // When
        underTest.getMenuGroups();

        // Then
        verify(menuGroupRepository).findAll();
    }

    @Test
    void getMenuGroupById_shouldReturnMenuGroupById_whenMenuGroupFound() {
        // Arrange
        when(menuGroupRepository.findById(MENU_GROUP_ID)).thenReturn(Optional.of(defaultMenuGroup));

        // Act
        underTest.getMenuGroupById(MENU_GROUP_ID);

        // Assert
        verify(menuGroupRepository).findById(MENU_GROUP_ID);
        assertThat(underTest.getMenuGroupById(MENU_GROUP_ID).name()).isEqualTo("Main Course");
    }

    @Test
    void getMenuGroupById_shouldThrowAnException_whenMenuGroupNotFound() {
        // Arrange
        when(menuGroupRepository.findById(MENU_GROUP_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> underTest.getMenuGroupById(MENU_GROUP_ID))
                .isInstanceOf(MenuGroupNotFoundException.class)
                .hasMessage("Menu group with id " + MENU_GROUP_ID + " not found");
    }

    @Test
    void addMenuGroup_shouldAddMenuGroup_whenMenuGroupNotNull() {
        // Arrange
        ArgumentCaptor<MenuGroup> captor = ArgumentCaptor.forClass(MenuGroup.class);

        // Act
        underTest.addMenuGroup(new CreateMenuGroupDto(
                "Main Course"
        ));

        // Assert
        verify(menuGroupRepository).save(captor.capture());
        MenuGroup capturedMenuGroup = captor.getValue();

        assertThat(capturedMenuGroup.getName()).isEqualTo("Main Course");
    }

    @Test
    void updateMenuGroup_shouldUpdateMenuGroup_whenMenuGroupFound() {
        // Arrange
        when(menuGroupRepository.findById(MENU_GROUP_ID)).thenReturn(Optional.of(defaultMenuGroup));
        when(menuGroupRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ArgumentCaptor<MenuGroup> captor = ArgumentCaptor.forClass(MenuGroup.class);

        // Act
        underTest.updateMenuGroupName(MENU_GROUP_ID, new UpdateMenuGroupDto(
                "Appetizers"
        ));

        // Assert
        verify(menuGroupRepository).save(captor.capture());
        MenuGroup capturedMenuGroup = captor.getValue();

        assertThat(capturedMenuGroup.getName()).isEqualTo("Appetizers");
    }

    @Test
    void updateMenuGroup_shouldThrowAnException_whenMenuGroupNotFound() {
        // Arrange
        when(menuGroupRepository.findById(MENU_GROUP_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> underTest.updateMenuGroupName(MENU_GROUP_ID, new UpdateMenuGroupDto(
                "Appetizers"
        )))
                .isInstanceOf(MenuGroupNotFoundException.class)
                .hasMessage("Menu group with id " + MENU_GROUP_ID + " not found");
    }

    @Test
    void deleteMenuGroup_shouldDeleteMenuGroup_whenMenuGroupFound() {
        // Arrange
        when(menuGroupRepository.existsById(MENU_GROUP_ID)).thenReturn(true);

        // Act
        underTest.deleteMenuGroup(MENU_GROUP_ID);

        // Assert
        verify(menuGroupRepository).deleteById(MENU_GROUP_ID);
    }

    @Test
    void deleteMenuGroup_shouldThrowException_whenMenuGroupNotFound() {
        // Arrange
        when(menuGroupRepository.existsById(MENU_GROUP_ID)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> underTest.deleteMenuGroup(MENU_GROUP_ID))
                .isInstanceOf(MenuGroupNotFoundException.class)
                .hasMessage("Menu group with id " + MENU_GROUP_ID + " not found");
    }
}
