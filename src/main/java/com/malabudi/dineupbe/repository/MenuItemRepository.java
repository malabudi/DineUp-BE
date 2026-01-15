package com.malabudi.dineupbe.repository;

import com.malabudi.dineupbe.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {

}
