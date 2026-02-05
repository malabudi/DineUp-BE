package com.malabudi.dineupbe.order.model;

import com.malabudi.dineupbe.common.util.OrderStatus;
import com.malabudi.dineupbe.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineItem> lineItems = new ArrayList<>();

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private BigDecimal total;

    public Order() {
        this.orderStatus = OrderStatus.PENDING;
        this.orderDate = LocalDateTime.now();
        this.total = BigDecimal.ZERO;
    }

    public void calculateTotalPrice() {
        this.total = lineItems.stream()
                .map(LineItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
