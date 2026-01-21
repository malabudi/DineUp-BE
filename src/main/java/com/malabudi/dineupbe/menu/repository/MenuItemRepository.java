package com.malabudi.dineupbe.menu.repository;

import com.malabudi.dineupbe.menu.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {
    Optional<MenuItem> findByName(String name);
}
