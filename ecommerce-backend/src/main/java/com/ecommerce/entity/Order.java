package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 👤 USER
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // 📦 ORDER ITEMS (🔥 FIXED)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items;

    private double totalPrice;
    private String address;
    private String paymentStatus;

    // ✅ DEFAULT CONSTRUCTOR
    public Order() {}

    // 🔥 OPTIONAL HELPER (GOOD PRACTICE)
    public void addItem(OrderItem item) {
        item.setOrder(this);
        this.items.add(item);
    }
}