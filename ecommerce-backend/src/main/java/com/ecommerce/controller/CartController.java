package com.ecommerce.controller;

import com.ecommerce.entity.Cart;
import com.ecommerce.service.CartService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    // 🔥 ADD TO CART
    @PostMapping("/add")
    public Cart addToCart(@RequestParam Long productId,
                          @RequestParam int quantity,
                          Authentication auth) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        String username = auth.getName();
        return service.addToCart(username, productId, quantity);
    }

    // 🔥 GET USER CART
    @GetMapping
    public List<Cart> getCart(Authentication auth) {

        String username = auth.getName();
        return service.getUserCart(username);
    }

    // 🔥 REMOVE ITEM
    @DeleteMapping("/{id}")
    public void remove(@PathVariable Long id) { // ✅ FIX
        service.removeItem(id);
    }

    // 🔥 UPDATE QUANTITY
    @PutMapping("/{id}")
    public Cart update(@PathVariable Long id,
                       @RequestParam int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        return service.updateQuantity(id, quantity);
    }
}