package com.malabudi.dineupbe.order.repository;

import com.malabudi.dineupbe.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> { }
