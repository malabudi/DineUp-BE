package com.malabudi.dineupbe.order.repository;

import com.malabudi.dineupbe.order.model.Order;
import com.malabudi.dineupbe.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {
    Optional<Order> findByCustomer(User customer);
}
