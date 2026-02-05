package com.malabudi.dineupbe.order.repository;

import com.malabudi.dineupbe.order.model.LineItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineItemRepository extends JpaRepository<LineItem,Long> {}
