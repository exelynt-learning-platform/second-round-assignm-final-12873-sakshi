package com.ecommerce.entity;

import jakarta.persistence.*;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Product reference
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // 🔢 Quantity
    private int quantity;

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}