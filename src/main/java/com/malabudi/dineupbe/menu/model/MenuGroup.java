package com.malabudi.dineupbe.menu.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "menu_groups")
public class MenuGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "menuGroup", cascade = CascadeType.ALL)
    private List<MenuItem> menuItems = new ArrayList<>();

    public MenuGroup(String name, List<MenuItem> menuItems) {
        this.name = name;
        this.menuItems = menuItems;
    }
    public MenuGroup(String name) {
        this.name = name;
    }

    public MenuGroup() {

    }
}
