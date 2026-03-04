package com.dineup.menu.service;

import com.dineup.menu.dto.CreateMenuGroupDto;
import com.dineup.menu.dto.ResponseMenuGroupDto;
import com.dineup.menu.dto.UpdateMenuGroupDto;
import com.dineup.menu.exception.MenuGroupNotFoundException;
import com.dineup.menu.mapper.MenuGroupMapper;
import com.dineup.menu.model.MenuGroup;
import com.dineup.menu.repository.MenuGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuGroupService {

    private final MenuGroupRepository menuGroupRepository;

    MenuGroupService(MenuGroupRepository menuGroupRepository) {
        this.menuGroupRepository = menuGroupRepository;
    }

    public List<ResponseMenuGroupDto> getMenuGroups() {
        return menuGroupRepository.findAll()
                .stream()
                .map(MenuGroupMapper::toDto)
                .toList();
    }

    public ResponseMenuGroupDto getMenuGroupById(Long id) {
        return  MenuGroupMapper.toDto(
                menuGroupRepository.findById(id)
                        .orElseThrow(() -> new MenuGroupNotFoundException(id))
        );
    }

    public ResponseMenuGroupDto addMenuGroup(CreateMenuGroupDto createMenuGroupDto) {
        MenuGroup menuGroup = new MenuGroup(
                createMenuGroupDto.name()
        );
        menuGroupRepository.save(menuGroup);

        return  MenuGroupMapper.toDto(menuGroup);
    }

    public ResponseMenuGroupDto updateMenuGroupName(Long id, UpdateMenuGroupDto updateMenuGroupDto) {
        MenuGroup menuGroup = menuGroupRepository.findById(id)
                .orElseThrow(() -> new MenuGroupNotFoundException(id));

        menuGroup.setName(updateMenuGroupDto.name());
        MenuGroup updatedMenuGroup = menuGroupRepository.save(menuGroup);
        return MenuGroupMapper.toDto(updatedMenuGroup);
    }

    public void deleteMenuGroup(Long id) {
        if (!menuGroupRepository.existsById(id)) {
            throw new MenuGroupNotFoundException(id);
        }

        menuGroupRepository.deleteById(id);
    }
}
