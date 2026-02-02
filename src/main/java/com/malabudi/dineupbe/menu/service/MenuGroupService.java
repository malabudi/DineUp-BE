package com.malabudi.dineupbe.menu.service;

import com.malabudi.dineupbe.menu.dto.MenuGroupDto;
import com.malabudi.dineupbe.menu.mapper.MenuGroupMapper;
import com.malabudi.dineupbe.menu.model.MenuGroup;
import com.malabudi.dineupbe.menu.repository.MenuGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuGroupService {
    private final MenuGroupRepository menuGroupRepository;
    private final MenuGroupMapper menuGroupMapper;

    MenuGroupService(MenuGroupRepository menuGroupRepository,  MenuGroupMapper menuGroupMapper) {
        this.menuGroupRepository = menuGroupRepository;
        this.menuGroupMapper = menuGroupMapper;
    }

    public List<MenuGroupDto> getMenuGroups() {
        return menuGroupRepository.findAll()
                .stream()
                .map(menuGroupMapper::toDto)
                .collect(Collectors.toList());
    }

    public MenuGroupDto getMenuGroupByName(String name) {
        return  menuGroupMapper.toDto(
                menuGroupRepository.findByName(name).orElseThrow()
        );
    }

    public void addMenuGroup(MenuGroupDto menuGroupDto) {
        MenuGroup menuGroup = menuGroupMapper.toEntity(menuGroupDto);
        menuGroupRepository.save(menuGroup);
    }
}
