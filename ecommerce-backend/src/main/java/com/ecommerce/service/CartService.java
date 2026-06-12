package com.ecommerce.service;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    public CartService(CartRepository cartRepo,
                       ProductRepository productRepo,
                       UserRepository userRepo) {
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }

    // 🔥 ADD TO CART
    public Cart addToCart(String username, Long productId, int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // ✅ STOCK VALIDATION
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        Cart cartItem = cartRepo.findByUserAndProduct(user, product)
                .orElse(null);

        if (cartItem != null) {

            int newQuantity = cartItem.getQuantity() + quantity;

            if (product.getStockQuantity() < newQuantity) {
                throw new RuntimeException("Insufficient stock");
            }

            cartItem.setQuantity(newQuantity);

        } else {
            cartItem = new Cart(user, product, quantity);
        }

        return cartRepo.save(cartItem);
    }

    // 🔥 GET USER CART
    public List<Cart> getUserCart(String username) {

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cartRepo.findByUser(user);
    }

    // 🔥 REMOVE ITEM
    public void removeItem(Long cartId) {

        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        cartRepo.delete(cart);
    }

    // 🔥 UPDATE QUANTITY
    public Cart updateQuantity(Long cartId, int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // ✅ FIXED HERE
        Product product = cart.getProduct();

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        cart.setQuantity(quantity);
        return cartRepo.save(cart);
    }
}