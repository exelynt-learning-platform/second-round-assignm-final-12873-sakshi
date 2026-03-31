package com.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ NAME
    @NotBlank(message = "Product name is required")
    @Column(nullable = false)
    private String name;

    private String description;

    // ✅ PRICE
    @Positive(message = "Price must be greater than 0")
    @Column(nullable = false)
    private double price;

    // ✅ STOCK
    @Min(value = 0, message = "Stock cannot be negative")
    @Column(nullable = false)
    private int stockQuantity;

    // ✅ IMAGE URL
    @NotBlank(message = "Image URL is required")
    @Column(nullable = false)
    private String imageUrl;

    // ✅ DEFAULT CONSTRUCTOR (IMPORTANT FOR JPA)
    public Product() {}

    // ✅ OPTIONAL CONSTRUCTOR
    public Product(String name, String description,
                   double price, int stockQuantity, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
    }

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    // 🔥 REQUIRED (JPA + tests)
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}