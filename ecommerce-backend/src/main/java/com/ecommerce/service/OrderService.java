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
    private final ProductRepository productRepo;

    public OrderService(OrderRepository orderRepo,
                        CartRepository cartRepo,
                        UserRepository userRepo,
                        ProductRepository productRepo) {
        this.orderRepo = orderRepo;
        this.cartRepo = cartRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
    }

    // 🔥 CREATE ORDER FROM CART
    public Order createOrder(String username, String address) {

        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address cannot be empty");
        }

        // ✅ USER VALIDATION
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ CART FETCH
        List<Cart> cartItems = cartRepo.findByUser(user);

        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double total = 0;

        // ✅ CREATE ORDER FIRST (needed for mapping)
        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setPaymentStatus("PENDING");

        // ✅ CONVERT CART → ORDER ITEMS
        List<OrderItem> items = cartItems.stream().map(c -> {

            // ✅ FIXED HERE
            Product product = c.getProduct();

            if (product == null) {
                throw new RuntimeException("Product not found in cart");
            }

            if (c.getQuantity() <= 0) {
                throw new RuntimeException("Invalid quantity for product: " + product.getName());
            }

            // 🔥 STOCK VALIDATION
            if (product.getStockQuantity() < c.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for product: " + product.getName());
            }

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(c.getQuantity());

            // 🔥 IMPORTANT FIX
            item.setOrder(order);

            return item;

        }).toList();

        // ✅ TOTAL + STOCK REDUCTION
        for (OrderItem item : items) {
            Product p = item.getProduct();

            total += p.getPrice() * item.getQuantity();

            p.setStockQuantity(p.getStockQuantity() - item.getQuantity());

            // 🔥 SAVE STOCK UPDATE
            productRepo.save(p);
        }

        order.setItems(items);
        order.setTotalPrice(total);

        Order savedOrder = orderRepo.save(order);

        // ✅ CLEAR CART
        cartRepo.deleteAll(cartItems);

        return savedOrder;
    }

    // 🔥 GET USER ORDERS
    public List<Order> getUserOrders(String username) {

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepo.findByUser(user);
    }

    // 🔥 PAYMENT STATUS UPDATE
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