package com.ecommerce.dto;

public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private double price;
    private int stockQuantity;
    private String imageUrl;

    // ✅ Default constructor (IMPORTANT)
    public ProductResponse() {}

    // ✅ All args constructor
    public ProductResponse(Long id, String name, String description,
                           double price, int stockQuantity, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
    }

    // ✅ GETTERS
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    public String getImageUrl() { return imageUrl; }

    // ✅ SETTERS (🔥 IMPORTANT FIX)
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}