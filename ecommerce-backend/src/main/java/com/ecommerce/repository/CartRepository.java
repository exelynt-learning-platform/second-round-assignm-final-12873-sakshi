package com.ecommerce.repository;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.User;
import com.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // 👉 Get all cart items for a user
    List<Cart> findByUser(User user);

    // 👉 Check if product already exists in user's cart
    Optional<Cart> findByUserAndProduct(User user, Product product);

    // 🔥 ADD THIS (FOR CART CLEAR)
    void deleteByUser(User user);
}