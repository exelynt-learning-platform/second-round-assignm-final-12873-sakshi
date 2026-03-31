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

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 👉 Check if already exists
        Cart cartItem = cartRepo.findByUserAndProduct(user, product)
                .orElse(null);

        if (cartItem != null) {
            // 👉 Increase quantity
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            // 👉 Create new
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
        cartRepo.deleteById(cartId);
    }

    // 🔥 UPDATE QUANTITY
    public Cart updateQuantity(Long cartId, int quantity) {

        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        cart.setQuantity(quantity);
        return cartRepo.save(cart);
    }
}