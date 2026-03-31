package com.ecommerce.entity;

import jakarta.persistence.*;

@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 👤 User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 📦 Product
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // 🔢 Quantity
    @Column(nullable = false)
    private int quantity;

    // 🔹 Constructors
    public Cart() {}

    public Cart(User user, Product product, int quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
    }

    // 🔹 Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {   // ✅ Added
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {  // ✅ Correct
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