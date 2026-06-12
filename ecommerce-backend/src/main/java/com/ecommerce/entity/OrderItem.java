package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Product reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // 🔗 Order reference (🔥 IMPORTANT FIX)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // 🔢 Quantity
    private int quantity;

    // ✅ DEFAULT CONSTRUCTOR
    public OrderItem() {}

    // ✅ OPTIONAL CONSTRUCTOR
    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}