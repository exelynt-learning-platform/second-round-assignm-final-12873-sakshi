package com.ecommerce.service;

import com.ecommerce.entity.*;
import com.ecommerce.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final CartRepository cartRepo;
    private final UserRepository userRepo;

    public OrderService(OrderRepository orderRepo,
                        CartRepository cartRepo,
                        UserRepository userRepo) {
        this.orderRepo = orderRepo;
        this.cartRepo = cartRepo;
        this.userRepo = userRepo;
    }

    // 🔥 CREATE ORDER FROM CART
    public Order createOrder(String username, String address) {

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Cart> cartItems = cartRepo.findByUser(user);

        // ✅ FIX: null + empty check
        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double total = 0;

        // ✅ FIX: stock validation + safe calculation
        for (Cart c : cartItems) {

            if (c.getProduct() == null) {
                throw new RuntimeException("Product not found in cart");
            }

            Product product = c.getProduct();

            // 🔥 IMPORTANT: STOCK VALIDATION (REVIEW REQUIRED)
            if (product.getStockQuantity() < c.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for product: " + product.getName());
            }

            // ✅ CORRECT TOTAL (already correct but keeping explicit)
            total += product.getPrice() * c.getQuantity();
        }

        // 👉 Extract products
        List<Product> products = cartItems.stream()
                .map(Cart::getProduct)
                .toList();

        // 👉 Create order
        Order order = new Order();
        order.setUser(user);
        order.setProducts(products);
        order.setTotalPrice(total);
        order.setAddress(address);
        order.setPaymentStatus("PENDING");

        Order savedOrder = orderRepo.save(order);

        // 👉 Clear cart after order
        cartRepo.deleteAll(cartItems);

        return savedOrder;
    }

    // 🔥 GET USER ORDERS
    public List<Order> getUserOrders(String username) {

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepo.findByUser(user);
    }

    // 🔥 PAYMENT
    public Order makePayment(Long orderId, boolean success) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("SUCCESS".equals(order.getPaymentStatus())) {
            throw new RuntimeException("Payment already completed");
        }

        order.setPaymentStatus(success ? "SUCCESS" : "FAILED");

        return orderRepo.save(order);
    }
}