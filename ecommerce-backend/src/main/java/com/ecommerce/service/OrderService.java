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

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty ❌");
        }

        // 👉 Extract products
        List<Product> products = cartItems.stream()
                .map(Cart::getProduct)
                .toList();

        // 👉 Calculate total
        double total = cartItems.stream()
                .mapToDouble(c -> c.getProduct().getPrice() * c.getQuantity())
                .sum();

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

        if (success) {
            order.setPaymentStatus("SUCCESS");
        } else {
            order.setPaymentStatus("FAILED");
        }

        return orderRepo.save(order);
    }
}