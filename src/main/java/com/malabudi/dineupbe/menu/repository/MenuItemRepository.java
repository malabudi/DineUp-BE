package com.malabudi.dineupbe.menu.repository;

import com.malabudi.dineupbe.menu.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> { }
