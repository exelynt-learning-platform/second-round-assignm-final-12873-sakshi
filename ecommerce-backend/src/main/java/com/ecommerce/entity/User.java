package com.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ USERNAME
    @NotBlank(message = "Username is required")
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    // ✅ PASSWORD
    @NotBlank(message = "Password is required")
    @Size(max = 128, message = "Password too long")
    @Column(nullable = false, length = 128)
    private String password;

    // ✅ ROLE
    @NotBlank(message = "Role is required")
    @Column(nullable = false)
    private String role;

    // ✅ WALLET AMOUNT
    @Column(nullable = false)
    private double amount = 0.0;

    // ✅ DEFAULT CONSTRUCTOR (IMPORTANT)
    public User() {}

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    // 🔥 REQUIRED (tests + JPA)
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}