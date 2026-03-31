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

        String username = auth.getName(); // 🔑 JWT se username
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
    public String remove(@PathVariable Long id) {

        service.removeItem(id);
        return "Item removed";
    }

    // 🔥 UPDATE QUANTITY
    @PutMapping("/{id}")
    public Cart update(@PathVariable Long id,
                       @RequestParam int quantity) {

        return service.updateQuantity(id, quantity);
    }
}