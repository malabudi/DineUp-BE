package com.dineup.menu.repository;

import com.dineup.menu.dto.MenuItemSalesDto;
import com.dineup.menu.model.MenuItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    @Query("""
        SELECT li.menuItem.id AS menuItemId,
               li.menuItem.name as menuItemName,
               SUM(li.quantity) AS totalQuantitySold
        FROM LineItem li
        WHERE li.order.orderStatus = "COMPLETE"
        GROUP BY li.menuItem.id, li.menuItem.name
        ORDER BY totalQuantitySold DESC
        """)
    List<MenuItemSalesDto> findBestSellingMenuItems(Pageable pageable);
}
