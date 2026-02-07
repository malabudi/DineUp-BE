package com.malabudi.dineupbe.menu.service;

import com.malabudi.dineupbe.menu.dto.CreateMenuGroupDto;
import com.malabudi.dineupbe.menu.dto.ResponseMenuGroupDto;
import com.malabudi.dineupbe.menu.dto.UpdateMenuGroupDto;
import com.malabudi.dineupbe.menu.exception.MenuGroupNotFoundException;
import com.malabudi.dineupbe.menu.mapper.MenuGroupMapper;
import com.malabudi.dineupbe.menu.model.MenuGroup;
import com.malabudi.dineupbe.menu.repository.MenuGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
                .collect(Collectors.toList());
    }

    public ResponseMenuGroupDto getMenuGroupById(Long id) {
        return  MenuGroupMapper.toDto(
                menuGroupRepository.findById(id)
                        .orElseThrow(MenuGroupNotFoundException::new)
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
                .orElseThrow(MenuGroupNotFoundException::new);

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
