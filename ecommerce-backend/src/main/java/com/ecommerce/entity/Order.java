package com.ecommerce.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 👤 User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 📦 Products
    @ManyToMany
    @JoinTable(
            name = "order_products",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    // 💰 Total Price
    private double totalPrice;

    // 🚚 Shipping Address
    private String address;

    // 💳 Payment Status
    private String paymentStatus;

    // 🔹 Constructors
    public Order() {}

    public Order(User user, List<Product> products,
                 double totalPrice, String address, String paymentStatus) {
        this.user = user;
        this.products = products;
        this.totalPrice = totalPrice;
        this.address = address;
        this.paymentStatus = paymentStatus;
    }

    // 🔹 Getters & Setters

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}